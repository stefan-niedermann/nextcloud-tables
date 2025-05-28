package it.niedermann.nextcloud.tables.repository.sync.treesync;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.remote.ocs.OcsAPI;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchProvider;
import it.niedermann.nextcloud.tables.repository.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.ocs.OcsSearchProviderMapper;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;

class SearchProviderSyncAdapter extends AbstractPullOnlySyncAdapter {

    private final RemoteMapper<OcsSearchProvider, SearchProvider> searchProviderMapper;

    public SearchProviderSyncAdapter(@NonNull Context context,
                                     @Nullable SyncStatusReporter reporter) {
        this(context, reporter, OcsSearchProviderMapper.INSTANCE);
    }

    public SearchProviderSyncAdapter(@NonNull Context context,
                                     @Nullable SyncStatusReporter reporter,
                                     @NonNull RemoteMapper<OcsSearchProvider, SearchProvider> searchProviderMapper) {
        super(context, reporter);
        this.searchProviderMapper = searchProviderMapper;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account,
                                                     @NonNull Account entity) {
        return requestHelper.executeOcsRequest(entity, OcsAPI::getSearchProviders)
                .thenApplyAsync(response -> switch (response.code()) {
                    case 200 -> {
                        final var body = response.body();
                        if (body == null) {
                            throwError(new IOException("Response body is null"));
                        }

                        assert body != null;
                        switch (body.ocs.meta.statusCode) {
                            case 500 ->
                                    throwError(new ServerNotAvailableException(ServerNotAvailableException.Reason.SERVER_ERROR));
                            case 503 ->
                                    throwError(new ServerNotAvailableException(ServerNotAvailableException.Reason.MAINTENANCE_MODE));
                            default -> {
                            }
                        }

                        yield searchProviderMapper.toEntityList(body.ocs.data);
                    }
                    default -> {
                        final var exception = serverErrorHandler.responseToException(response, "Could not fetch " + SearchProvider.class.getSimpleName() + " for " + entity.getAccountName(), true);

                        exception.ifPresent(this::throwError);

                        yield Collections.<SearchProvider>emptyList();
                    }
                }, workExecutor)
                .thenApplyAsync(sp -> new Pair<>(sp, db.getSearchProviderDao()
                        .getRemoteIdToLocalId(account.getId())), db.getSyncReadExecutor())
                .thenApplyAsync(args -> {
                    final var searchProviders = args.first;
                    final var remoteIdToSearchProviders = args.second;
                    return searchProviders
                            .stream()
                            .peek(provider -> Optional.of(provider.getRemoteId())
                                    .map(remoteIdToSearchProviders::get)
                                    .ifPresent(provider::setId))
                            .peek(provider -> provider.setAccountId(account.getId()))
                            .toArray(SearchProvider[]::new);
                }, workExecutor)
                .thenAcceptAsync(db.getSearchProviderDao()::upsert, db.getSyncWriteExecutor());
    }
}
