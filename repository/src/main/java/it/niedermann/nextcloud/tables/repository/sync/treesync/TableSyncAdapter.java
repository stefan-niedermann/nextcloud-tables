package it.niedermann.nextcloud.tables.repository.sync.treesync;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.TableV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.RemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.TableV2Mapper;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;
import retrofit2.Response;


class TableSyncAdapter extends AbstractSyncAdapter<Account> {

    private static final Logger logger = Logger.getLogger(TableSyncAdapter.class.getSimpleName());

    private final SyncAdapter<Table> columnSyncAdapter;
    private final SyncAdapter<Table> rowSyncAdapter;
    private final RemoteMapper<TableV2Dto, Table> tableMapper;

    protected TableSyncAdapter(@NonNull Context context,
                               @Nullable SyncStatusReporter reporter) {
        this(context, reporter, TableV2Mapper.INSTANCE, new ColumnSyncAdapter(context, reporter), new RowSyncAdapter(context, reporter));
    }

    private TableSyncAdapter(@NonNull Context context,
                             @Nullable SyncStatusReporter reporter,
                             @NonNull RemoteMapper<TableV2Dto, Table> tableMapper,
                             @NonNull SyncAdapter<Table> columnSyncAdapter,
                             @NonNull SyncAdapter<Table> rowSyncAdapter) {
        super(context, reporter);
        this.tableMapper = tableMapper;
        this.columnSyncAdapter = columnSyncAdapter;
        this.rowSyncAdapter = rowSyncAdapter;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalCreations(@NonNull Account account, @NonNull Account parentEntity) {
        return completedFuture(account.getId())
                .thenApplyAsync(db.getTableDao()::getLocallyCreatedTables, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tablesToCreate -> tablesToCreate
                        .map(table -> completedFuture(null)
                                .thenComposeAsync(v -> this.createRemote(account, table), workExecutor)
                                .thenComposeAsync(response -> this.markLocallyAsUpdated(table, response), workExecutor)
                                .thenComposeAsync(v -> this.columnSyncAdapter.pushLocalCreations(account, table), workExecutor)
                                .thenComposeAsync(v -> this.rowSyncAdapter.pushLocalCreations(account, table), workExecutor)
                                .handleAsync(provideDebugContext(table), workExecutor)
                        ), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor);
    }

    @NonNull
    private CompletableFuture<Response<OcsResponse<TableV2Dto>>> createRemote(@NonNull Account account, @NonNull Table entity) {
        return checkRemoteIdNull(entity.getRemoteId())
                .thenComposeAsync(v -> requestHelper.executeTablesV2Request(account, api -> api.createTable(
                        entity.getTitle(),
                        Optional.ofNullable(entity.getDescription()).orElse(""),
                        entity.getEmoji(),
                        TablesV2API.DEFAULT_TABLES_TEMPLATE)), workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalUpdates(@NonNull Account account, @NonNull Account parentEntity) {
        return completedFuture(account.getId())
                .thenApplyAsync(db.getTableDao()::getLocallyEditedTables, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tableToPush -> tableToPush
                        .map(table -> completedFuture(null)
                                .thenComposeAsync(v -> this.updateRemote(account, table), workExecutor)
                                .thenComposeAsync(response -> this.markLocallyAsUpdated(table, response), workExecutor)
                                .thenComposeAsync(v -> this.columnSyncAdapter.pushLocalUpdates(account, table), workExecutor)
                                .thenComposeAsync(v -> this.rowSyncAdapter.pushLocalUpdates(account, table), workExecutor)
                                .handleAsync(provideDebugContext(table), workExecutor)
                        ), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor);
    }

    @NonNull
    private CompletableFuture<Response<OcsResponse<TableV2Dto>>> updateRemote(@NonNull Account account, @NonNull Table entity) {
        return checkRemoteIdNotNull(entity.getRemoteId())
                // TODO This can theoretically run parallel and is not always necessary
                .thenComposeAsync(v -> requestHelper.executeTablesV2Request(account, api -> entity.isFavorite()
                        ? api.setFavorite(ENodeTypeV2Dto.TABLE.id, requireNonNull(entity.getRemoteId()))
                        : api.unsetFavorite(ENodeTypeV2Dto.TABLE.id, requireNonNull(entity.getRemoteId()))), workExecutor)
                .thenComposeAsync(v -> requestHelper.executeTablesV2Request(account, api -> api.updateTable(
                        requireNonNull(entity.getRemoteId()),
                        entity.getTitle(),
                        entity.getEmoji(),
                        entity.isArchived() ? 1 : 0,
                        Optional.ofNullable(entity.getDescription()).orElse(""))), workExecutor);
    }

    @NonNull
    private CompletableFuture<Void> markLocallyAsUpdated(@NonNull Table entity, @NonNull Response<OcsResponse<TableV2Dto>> response) {
        logger.info(() -> "-→ HTTP " + response.code());
        if (response.isSuccessful()) {

            final var body = response.body();

            if (body == null || body.ocs == null || body.ocs.data == null) {
                throwError(new NullPointerException("Pushing changes for table " + entity + " was successful, but response body was empty"));
            }

            assert body != null;

            entity.setRemoteId(body.ocs.data.remoteId());
            entity.setStatus(DBStatus.VOID);

            return runAsync(() -> db.getTableDao().update(entity), db.getSyncWriteExecutor());

        } else {
            serverErrorHandler.responseToException(response, "Could not push local changes for table " + entity, false).ifPresent(this::throwError);
            return completedFuture(null);
        }
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalDeletions(@NonNull Account account, @NonNull Account parentEntity) {
        return completedFuture(account.getId())
                .thenApplyAsync(db.getTableDao()::getLocallyDeletedTables, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tablesToUpdate -> tablesToUpdate
                        .map(table -> {
                            final var future = table.getRemoteId() == null
                                    // Table has never been pushed to remote, so it is safe to simply delete it on the client
                                    ? runAsync(() -> db.getTableDao().delete(table), db.getSyncWriteExecutor())
                                    : this.deleteRemote(account, table)
                                    .thenComposeAsync(response -> this.deleteLocallyPhysically(table, response), workExecutor)
                                    .handleAsync(provideDebugContext(table), workExecutor);

                            return future
                                    .thenComposeAsync(v -> this.columnSyncAdapter.pushLocalDeletions(account, table), workExecutor)
                                    .thenComposeAsync(v -> this.rowSyncAdapter.pushLocalDeletions(account, table), workExecutor)
                                    .handleAsync(provideDebugContext(table), workExecutor);

                        }), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor);
    }

    @NonNull
    private CompletableFuture<Response<OcsResponse<TableV2Dto>>> deleteRemote(@NonNull Account account, @NonNull Table entity) {
        return checkRemoteIdNotNull(entity.getRemoteId())
                .thenComposeAsync(remoteId -> requestHelper.executeTablesV2Request(account, api -> api.deleteTable(remoteId)), workExecutor);
    }

    private CompletableFuture<Void> deleteLocallyPhysically(@NonNull Table entity, @NonNull Response<?> response) {
        return completedFuture(null)
                .thenComposeAsync(v -> {
                    logger.info("-→ HTTP " + response.code());

                    if (response.isSuccessful() || response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                        return runAsync(() -> db.getTableDao().delete(entity), db.getSyncWriteExecutor());

                    } else {
                        serverErrorHandler.responseToException(response, "Could not delete table " + entity, false).ifPresent(this::throwError);
                        return completedFuture(null);
                    }
                }, workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushChildChangesWithoutChangedParent(@NonNull Account account) {
        return completedFuture(account.getId())

                .thenApplyAsync(db.getColumnDao()::getUnchangedTablesHavingLocallyDeletedColumns, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tables -> tables.map(table -> columnSyncAdapter.pushLocalDeletions(account, table)
                        .handleAsync(provideDebugContext(table), workExecutor)), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor)

                .thenApplyAsync(v -> account.getId(), workExecutor)

                .thenApplyAsync(db.getColumnDao()::getUnchangedTablesHavingLocallyCreatedColumns, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tables -> tables.map(table -> columnSyncAdapter.pushLocalCreations(account, table)
                        .handleAsync(provideDebugContext(table), workExecutor)), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor)

                .thenApplyAsync(v -> account.getId(), workExecutor)

                .thenApplyAsync(db.getColumnDao()::getUnchangedTablesHavingLocallyEditedColumnsOrChangedOrDeletedSelectionOptions, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tables -> tables.map(table -> columnSyncAdapter.pushLocalUpdates(account, table)
                        .handleAsync(provideDebugContext(table), workExecutor)), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor)

                .thenApplyAsync(v -> account, workExecutor)

                .thenComposeAsync(columnSyncAdapter::pushChildChangesWithoutChangedParent, workExecutor)

                .thenApplyAsync(v -> account.getId(), workExecutor)

                .thenApplyAsync(db.getRowDao()::getUnchangedTablesHavingLocallyDeletedRows, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tables -> tables.map(table -> rowSyncAdapter.pushLocalDeletions(account, table)
                        .handleAsync(provideDebugContext(table), workExecutor)), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor)

                .thenApplyAsync(v -> account.getId(), workExecutor)

                .thenApplyAsync(db.getRowDao()::getUnchangedTablesHavingLocallyCreatedRows, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tables -> tables.map(table -> rowSyncAdapter.pushLocalCreations(account, table)
                        .handleAsync(provideDebugContext(table), workExecutor)), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor)

                .thenApplyAsync(v -> account.getId(), workExecutor)

                .thenApplyAsync(db.getRowDao()::getUnchangedTablesHavingLocallyEditedRowsOrChangedOrDeletedData, db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(tables -> tables.map(table -> rowSyncAdapter.pushLocalUpdates(account, table)
                        .handleAsync(provideDebugContext(table), workExecutor)), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor)

                .thenApplyAsync(v -> account, workExecutor)

                .thenComposeAsync(rowSyncAdapter::pushChildChangesWithoutChangedParent, workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account,
                                                     @NonNull Account parentEntity) {
        logger.info(() -> getClass().getSimpleName() + "#pullRemoteChanges for " + account.getAccountName());
        return requestHelper.executeTablesV2Request(account, TablesV2API::getTables)
                .thenComposeAsync(response -> {
                    final Collection<Table> fetchedTables;
                    logger.info(() -> "Pulling remote changes for " + account.getAccountName());

                    //noinspection SwitchStatementWithTooFewBranches
                    switch (response.code()) {
                        case 200 -> {
                            final var responseBody = response.body();
                            if (responseBody == null || responseBody.ocs == null || responseBody.ocs.data == null) {
                                throw new RuntimeException("Response body is null");
                            }

                            Optional.ofNullable(reporter).ifPresent(r -> r.report(state -> state.withTableTotalCount(responseBody.ocs.data.size())));
                            fetchedTables = responseBody.ocs.data
                                    .stream()
                                    .map(tableMapper::toEntity)
                                    .peek(table -> {
                                        table.setStatus(DBStatus.VOID);
                                        table.setAccountId(account.getId());
                                        table.setETag(response.headers().get(HEADER_ETAG));
                                    })
                                    .collect(toUnmodifiableSet());
                        }
                        default -> {
                            fetchedTables = Collections.emptySet();
                            serverErrorHandler.responseToException(response, "", true).ifPresent(this::throwError);
                        }
                    }

                    final var tableRemoteIds = fetchedTables
                            .stream()
                            .map(AbstractRemoteEntity::getRemoteId)
                            .filter(Objects::nonNull)
                            .collect(toUnmodifiableSet());

                    return supplyAsync(() -> db.getTableDao().getTableRemoteAndLocalIds(account.getId(), tableRemoteIds), db.getSyncReadExecutor())
                            .thenApplyAsync(tableIds -> fetchedTables.stream()
                                    .map(table -> runAsync(() -> Optional.ofNullable(reporter).ifPresent(r -> r.report(state -> state.withTableProgressStarting(table))), workExecutor)
                                            .thenApplyAsync(v -> table.getRemoteId(), workExecutor)
                                            .thenApplyAsync(tableIds::get, workExecutor)
                                            .thenComposeAsync(tableId -> {
                                                if (tableId == null) {
                                                    logger.info(() -> "← Adding table " + table.getTitle() + " to database");

                                                    return completedFuture(table)
                                                            .thenApplyAsync(db.getTableDao()::insert, db.getSyncWriteExecutor())
                                                            .thenAcceptAsync(table::setId, workExecutor);

                                                } else {
                                                    table.setId(tableId);
                                                    logger.info(() -> "← Updating " + table.getTitle() + " in database");

                                                    return completedFuture(table)
                                                            .thenAcceptAsync(db.getTableDao()::update, db.getSyncWriteExecutor())
                                                            .thenRunAsync(() -> {
                                                                if (!table.hasReadPermission()) {
                                                                    db.getRowDao().deleteAllFromTable(table.getId());
                                                                }
                                                            }, db.getSyncWriteExecutor());
                                                }
                                            }, workExecutor)
                                            .thenComposeAsync(v -> columnSyncAdapter.pullRemoteChanges(account, table), workExecutor)
                                            .thenComposeAsync(v -> rowSyncAdapter.pullRemoteChanges(account, table), workExecutor)
                                            .thenRunAsync(() -> Optional.ofNullable(reporter).ifPresent(r -> r.report(state -> state.withTableProgressFinished(table))), workExecutor)
                                            .handleAsync(provideDebugContext(table), workExecutor)
                                    ), workExecutor)
                            .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                            .thenComposeAsync(CompletableFuture::allOf, workExecutor)
                            .thenAcceptAsync(v -> logger.info(() -> "← Delete all tables except remoteId " + tableRemoteIds), workExecutor)
                            .thenAcceptAsync(v -> db.getTableDao().deleteExcept(account.getId(), tableRemoteIds), db.getSyncWriteExecutor());
                }, workExecutor)
                .thenRunAsync(() -> db.getAccountDao().guessCurrentTable(account.getId()), db.getSyncWriteExecutor());
    }
}
