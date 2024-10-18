package it.niedermann.nextcloud.tables.repository.sync;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.NextcloudVersion;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.database.model.Version;
import it.niedermann.nextcloud.tables.repository.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.ocs.OcsVersionMapper;

class CapabilitiesSyncAdapter extends AbstractSyncAdapter {

    private final Mapper<OcsCapabilitiesResponse.OcsVersion, Version> versionMapper;

    public CapabilitiesSyncAdapter(@NonNull Context context) {
        super(context);
        this.versionMapper = new OcsVersionMapper();
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalChanges(@NonNull Account account) {
        // Users can't be changed locally
        return CompletableFuture.completedFuture(null);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account) {
        return executeNetworkRequest(account, apis -> apis.ocs().getCapabilities(account.getETag()))
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

                        final var nextcloudVersion = NextcloudVersion.of(versionMapper.toEntity(body.ocs.data.version()));
                        if (!nextcloudVersion.isSupported()) {
                            throwError(new ServerNotAvailableException(ServerNotAvailableException.Reason.TABLES_NOT_SUPPORTED));
                        }

                        final var tablesNode = body.ocs.data.capabilities().tables();
                        if (tablesNode == null) {
                            throwError(new ServerNotAvailableException(ServerNotAvailableException.Reason.NOT_INSTALLED));
                        }

                        assert tablesNode != null;
                        if (!tablesNode.enabled()) {
                            throwError(new ServerNotAvailableException(ServerNotAvailableException.Reason.NOT_ENABLED));
                        }

                        final var tablesVersion = TablesVersion.parse(tablesNode.version());
                        if (!tablesVersion.isSupported()) {
                            throwError(new ServerNotAvailableException(ServerNotAvailableException.Reason.TABLES_NOT_SUPPORTED));
                        }

                        account.setTablesVersion(tablesVersion);
                        account.setNextcloudVersion(nextcloudVersion);
                        account.setETag(response.headers().get(HEADER_ETAG));
                        account.setColor(Color.parseColor(ColorUtil.formatColorToParsableHexString(body.ocs.data.capabilities().theming().color)));
                        yield account;
                    }
                    default -> {
                        final var exception = serverErrorHandler.responseToException(response, "Could not fetch capabilities for " + account.getAccountName(), true);

                        exception.ifPresent(this::throwError);

                        yield account;
                    }
                })
                .thenAcceptAsync(db.getAccountDao()::update, db.getSequentialExecutor());
    }
}
