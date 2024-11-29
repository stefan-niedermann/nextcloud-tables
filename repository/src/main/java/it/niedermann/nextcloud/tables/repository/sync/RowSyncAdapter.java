package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.FetchRowResponseV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.CreateRowResponseV2Mapper;

class RowSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = RowSyncAdapter.class.getSimpleName();
    private final FetchRowResponseV1Mapper fetchRowV1Mapper;
    private final CreateRowResponseV2Mapper createRowV2Mapper;

    public RowSyncAdapter(@NonNull Context context) {
        this(context,
                new FetchRowResponseV1Mapper(),
                new CreateRowResponseV2Mapper());
    }

    private RowSyncAdapter(@NonNull Context context,
                           @NonNull FetchRowResponseV1Mapper fetchRowV1Mapper,
                           @NonNull CreateRowResponseV2Mapper createRowV2Mapper) {
        super(context);
        this.fetchRowV1Mapper = fetchRowV1Mapper;
        this.createRowV2Mapper = createRowV2Mapper;
    }

    @Override
    public @NonNull CompletableFuture<Account> pushLocalChanges(@NonNull Account account) {
        return supplyAsync(() -> db.getRowDao().getLocallyDeletedRows(account.getId()), db.getParallelExecutor())
                .thenComposeAsync(rowsToDelete -> {
                    Log.v(TAG, "------ Pushing " + rowsToDelete.size() + " local row deletions for " + account.getAccountName());

                    return CompletableFuture.allOf(rowsToDelete.stream()
                            .peek(row -> Log.i(TAG, "------ → DELETE: " + row.getRemoteId()))
                            .map(row -> {
                                if (row.getRemoteId() == null) {
                                    return runAsync(() -> db.getRowDao().delete(row), db.getSequentialExecutor());

                                } else {
                                    return executeNetworkRequest(account, apis -> apis.apiV1().deleteRow(row.getRemoteId()))
                                            .thenComposeAsync(response -> {
                                                Log.i(TAG, "------ → HTTP " + response.code());
                                                if (response.isSuccessful()) {
                                                    return runAsync(() -> db.getRowDao().delete(row), db.getSequentialExecutor());
                                                } else {
                                                    serverErrorHandler.responseToException(response, "Could not delete row " + row.getRemoteId(), false).ifPresent(this::throwError);
                                                    return completedFuture(null);
                                                }
                                            });
                                }
                            }).toArray(CompletableFuture[]::new));
                }, workExecutor)
                .thenApplyAsync(v -> account.getId(), workExecutor)
                .thenApplyAsync(db.getRowDao()::getLocallyEditedRows, db.getParallelExecutor())
                .thenComposeAsync(fullRowsToUpdate -> {
                    Log.v(TAG, "------ Pushing " + fullRowsToUpdate.size() + " local row changes for " + account.getAccountName());

                    final var version = account.getTablesVersion();

                    if (version == null) {
                        throw new IllegalStateException(TablesVersion.class.getSimpleName() + " is null. Capabilities need to be synchronized before pushing local changes.");
                    }

                    return CompletableFuture.allOf(fullRowsToUpdate.stream()
                            .peek(fullRow -> Log.i(TAG, "------ → PUT/POST: " + fullRow.getRow().getRemoteId()))
                            .map(fullRow -> {
                                if (fullRow.getRow().getRemoteId() == null) {
                                    final var createRowDto = createRowV2Mapper.toCreateRowV2Dto(version, fullRow.getFullData());
                                    // TODO maybe preload map of table local / remote IDs?
                                    return supplyAsync(() -> db.getTableDao().getRemoteId(fullRow.getRow().getTableId()), db.getParallelExecutor())
                                            .thenComposeAsync(tableRemoteId -> executeNetworkRequest(account, apis -> apis.apiV2().createRow(ENodeTypeV2Dto.TABLES, tableRemoteId, createRowDto)), workExecutor)
                                            .thenComposeAsync(response -> {
                                                if (response.isSuccessful()) {
                                                    fullRow.getRow().setStatus(DBStatus.VOID);
                                                    final var body = response.body();

                                                    if (body == null || body.ocs == null || body.ocs.data == null) {
                                                        throw new NullPointerException("Pushing changes for row " + fullRow.getRow().getRemoteId() + " was successfully, but response body was empty");
                                                    }

                                                    fullRow.getRow().setRemoteId(body.ocs.data.remoteId());
                                                    return runAsync(() -> db.getRowDao().update(fullRow.getRow()), db.getSequentialExecutor());

                                                } else {
                                                    serverErrorHandler.responseToException(response, "Could not push local changes for row " + fullRow.getRow().getRemoteId(), false).ifPresent(this::throwError);
                                                    return completedFuture(null);
                                                }
                                            });

                                } else {
                                    final var createRowDto = fetchRowV1Mapper.toJsonElement(version, fullRow.getFullData());
                                    return executeNetworkRequest(account, apis -> apis.apiV1().updateRow(fullRow.getRow().getRemoteId(), createRowDto))
                                            .thenComposeAsync(response -> {
                                                Log.i(TAG, "------ → HTTP " + response.code());

                                                if (response.isSuccessful()) {
                                                    fullRow.getRow().setStatus(DBStatus.VOID);
                                                    final var body = response.body();
                                                    if (body == null) {
                                                        throw new NullPointerException("Pushing changes for row " + fullRow.getRow().getRemoteId() + " was successfully, but response body was empty");
                                                    }

                                                    fullRow.getRow().setRemoteId(body.remoteId());
                                                    return runAsync(() -> db.getRowDao().update(fullRow.getRow()), db.getSequentialExecutor());

                                                } else {
                                                    serverErrorHandler.responseToException(response, "Could not push local changes for row " + fullRow.getRow().getRemoteId(), false).ifPresent(this::throwError);
                                                    return completedFuture(null);
                                                }
                                            });
                                }
                            }).toArray(CompletableFuture[]::new));
                }, workExecutor)
                .thenApplyAsync(v -> account, workExecutor);
    }

    @Override
    public @NonNull CompletableFuture<Account> pullRemoteChanges(@NonNull Account account) {
        return supplyAsync(() -> db.getTableDao().getTablesWithReadPermission(account.getId()), db.getParallelExecutor())
                .thenComposeAsync(tables -> {
                    final var version = account.getTablesVersion();
                    if (version == null) {
                        throw new IllegalStateException(TablesVersion.class.getSimpleName() + " is null. Capabilities need to be synchronized before pulling remote changes.");
                    }

                    return CompletableFuture.allOf(tables.stream().map(table -> supplyAsync(() -> new Pair<>(
                                    db.getColumnDao().getNotDeletedRemoteIdsAndColumns(table.getId()),
                                    db.getColumnDao().getColumnRemoteAndLocalIds(table.getId())
                            ), db.getParallelExecutor())
                                    .thenComposeAsync(columnsAndIdMap -> fetchAndPersistRows(
                                            account, table, 0,
                                            ConcurrentHashMap.newKeySet(columnsAndIdMap.second.values().size()),
                                            columnsAndIdMap.first,
                                            columnsAndIdMap.second), workExecutor)
                                    .thenApplyAsync(fetchedRowIds -> new Pair<>(fetchedRowIds, db.getRowDao().getIds(table.getId())), db.getParallelExecutor())
                                    .thenApplyAsync(pair -> {
                                        final var fetchedRowIds = pair.first;
                                        final var existingRowIds = pair.second;

                                        final var rowIdsToDelete = new HashSet<>(existingRowIds);
                                        rowIdsToDelete.removeAll(fetchedRowIds);
                                        Log.i(TAG, "------ ← Delete rows with local ID in " + rowIdsToDelete);

                                        return rowIdsToDelete;
                                    }, workExecutor)
                                    .thenAcceptAsync(existingRowIds -> existingRowIds.forEach(db.getRowDao()::delete), db.getSequentialExecutor())
                    ).toArray(CompletableFuture[]::new));
                })
                .thenApplyAsync(v -> account, workExecutor);
    }


    /// @return Collection of [Row#id]s that have been fetched and persisted
    @NonNull
    private CompletableFuture<Collection<Long>> fetchAndPersistRows(@NonNull final Account account,
                                                                    @NonNull final Table table,
                                                                    final int offset,
                                                                    @NonNull final Collection<Long> target,
                                                                    @NonNull final Map<Long, Column> columns,
                                                                    @NonNull final Map<Long, Long> columnRemoteAndLocalIds) {
        return this.getTableRemoteIdOrThrow(table, Row.class)
                .thenComposeAsync(tableRemoteId -> executeNetworkRequest(account, apis -> apis.apiV1().getRows(tableRemoteId, TablesV1API.DEFAULT_API_LIMIT_ROWS, offset)), workExecutor)
                .thenComposeAsync(response -> switch (response.code()) {
                    case 200: {
                        final var rowDtos = response.body();

                        if (rowDtos == null) {
                            throw new RuntimeException("Response body is null");
                        }

                        yield CompletableFuture.allOf(rowDtos.stream().map(rowDto -> supplyAsync(() -> {
                                    final var fullRow = fetchRowV1Mapper.toEntity(account.getId(), rowDto, columns, Objects.requireNonNull(account.getTablesVersion()));

                                    final var row = fullRow.getRow();
                                    row.setAccountId(table.getAccountId());
                                    row.setTableId(table.getId());
                                    row.setETag(response.headers().get(HEADER_ETAG));

                                    return fullRow;
                                }, workExecutor)
                                        .thenApplyAsync(fullRow -> new Pair<>(fullRow, Optional.of(fullRow)
                                                .map(FullRow::getRow)
                                                .map(Row::getRemoteId)
                                                .map(remoteId -> db.getRowDao().getRowId(table.getId(), remoteId))
                                        ), db.getParallelExecutor())
                                        .thenComposeAsync(rowAndRowId -> upsertRow(rowAndRowId.first, rowAndRowId.second.orElse(null)), workExecutor)
                                        .thenComposeAsync(fullRow -> CompletableFuture.allOf(fullRow
                                                        .getFullData().stream()
                                                        .map(FullData::getData)
                                                        .map(data -> upsertData(data, columnRemoteAndLocalIds))
                                                        .toArray(CompletableFuture[]::new))
                                                .thenComposeAsync(v -> {
                                                    target.add(fullRow.getRow().getId());
                                                    return CompletableFuture.<Void>completedFuture(null);
                                                }, workExecutor), workExecutor)).toArray(CompletableFuture[]::new))
                                .thenComposeAsync(v -> {
                                    if (rowDtos.size() < TablesV1API.DEFAULT_API_LIMIT_ROWS) {
                                        return completedFuture(target);
                                    }

                                    final var newOffset = offset + rowDtos.size();
                                    return fetchAndPersistRows(account, table, newOffset, target, columns, columnRemoteAndLocalIds);
                                }, workExecutor);
                    }

                    default: {
                        final var future = new CompletableFuture<Collection<Long>>();
                        serverErrorHandler
                                .responseToException(response, "Could not fetch rows for table with remote ID " + table.getRemoteId(), true)
                                .ifPresentOrElse(
                                        future::completeExceptionally,
                                        () -> future.complete(target));
                        yield future;
                    }
                }, workExecutor);
    }

    @NonNull
    public CompletableFuture<FullRow> upsertRow(@NonNull final FullRow fullRow,
                                                @Nullable Long potentialRowId) {
        return supplyAsync(fullRow::getRow, workExecutor)
                .thenComposeAsync(row -> {

                    if (potentialRowId == null) {

                        Log.i(TAG, "------ ← Adding row " + row.getRemoteId() + " to database");
                        return supplyAsync(() -> db.getRowDao().insert(row), db.getSequentialExecutor())
                                .thenAcceptAsync(row::setId, workExecutor)
                                .thenApplyAsync(v -> row.getId(), workExecutor);

                    } else {

                        row.setId(potentialRowId);

                        Log.i(TAG, "------ ← Updating row " + row.getRemoteId() + " in database");
                        return runAsync(() -> db.getRowDao().update(row), db.getSequentialExecutor())
                                .thenApplyAsync(v -> potentialRowId, workExecutor);
                    }

                }, workExecutor)
                .thenAcceptAsync(actualRowId -> fullRow.getFullData().stream()
                        .map(FullData::getData)
                        .forEach(data -> data.setRowId(actualRowId)), workExecutor)
                .thenApplyAsync(actualRowId -> fullRow, workExecutor);
    }

    @NonNull
    public CompletableFuture<Void> upsertData(@NonNull final Data data,
                                              @NonNull final Map<Long, Long> columnRemoteAndLocalIds) {
        return completedFuture(null)
                .thenComposeAsync(v -> {
                    if (columnRemoteAndLocalIds.containsKey(data.getRemoteColumnId())) {
                        final var existingData = Optional.ofNullable(db.getDataDao().getDataIdForCoordinates(data.getColumnId(), data.getRowId()));
                        if (existingData.isEmpty()) {
                            return supplyAsync(() -> db.getDataDao().insert(data), db.getSequentialExecutor())
                                    .thenAcceptAsync(data::setId);

                        } else {
                            data.setId(existingData.get());
                            return runAsync(() -> db.getDataDao().update(data), db.getSequentialExecutor());
                        }

                    } else {
                        // Data deletion is handled by database constraints
                        Log.w(TAG, "------ Could not find remoteColumnId " + data.getRemoteColumnId() + ". Probably this column has been deleted but its data is still being responded by the server (See https://github.com/nextcloud/tables/issues/257)");
                        return completedFuture(null);
                    }

                }, workExecutor);
    }
}