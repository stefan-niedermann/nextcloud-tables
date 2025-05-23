package it.niedermann.nextcloud.tables.repository.sync.treesync;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.model.ocs.OcsCapabilitiesResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.NextcloudVersion;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.database.model.Version;
import it.niedermann.nextcloud.tables.repository.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.ocs.OcsVersionMapper;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;

class CapabilitiesSyncAdapter extends AbstractPullOnlySyncAdapter {

    private final Mapper<OcsCapabilitiesResponse.OcsVersion, Version> versionMapper;

    public CapabilitiesSyncAdapter(@NonNull Context context) {
        this(context, null);
    }

    public CapabilitiesSyncAdapter(@NonNull Context context,
                                   @Nullable SyncStatusReporter reporter) {
        this(context, reporter, new OcsVersionMapper());
    }

    public CapabilitiesSyncAdapter(@NonNull Context context,
                                   @Nullable SyncStatusReporter reporter,
                                   @NonNull Mapper<OcsCapabilitiesResponse.OcsVersion, Version> versionMapper) {
        super(context, reporter);
        this.versionMapper = versionMapper;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account,
                                                     @NonNull Account entity) {
        return requestHelper.executeNetworkRequest(entity, apis -> apis.ocs().getCapabilities(entity.getCapabilitiesETag()))
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

                        entity.setTablesVersion(tablesVersion);
                        entity.setNextcloudVersion(nextcloudVersion);
                        entity.setCapabilitiesETag(response.headers().get(HEADER_ETAG));
                        entity.setColor(Color.parseColor(ColorUtil.formatColorToParsableHexString(body.ocs.data.capabilities().theming().color)));
                        yield Optional.of(entity);
                    }
                    default -> {
                        final var exception = serverErrorHandler.responseToException(response, "Could not fetch capabilities for " + entity.getAccountName(), true);

                        exception.ifPresent(this::throwError);

                        final var tablesVersion = Optional.ofNullable(account.getTablesVersion());
                        if (tablesVersion.map(TablesVersion::isSupported).map(Boolean.FALSE::equals).orElse(false)) {
                            throwError(new ServerNotAvailableException(ServerNotAvailableException.Reason.TABLES_NOT_SUPPORTED));
                        }

                        yield Optional.<Account>empty();
                    }
                })
                .thenAcceptAsync(accountWithNewCapabilities -> accountWithNewCapabilities.ifPresent(db.getAccountDao()::update), db.getSequentialWriteExecutorForSync());
    }
}
