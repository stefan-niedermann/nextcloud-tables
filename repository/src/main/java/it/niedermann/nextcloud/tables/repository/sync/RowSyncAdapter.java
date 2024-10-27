package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableSet;

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

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.FetchRowResponseV1Mapper;

class RowSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = RowSyncAdapter.class.getSimpleName();
    private final FetchRowResponseV1Mapper fetchRowMapper;

    public RowSyncAdapter(@NonNull Context context) {
        this(context, new FetchRowResponseV1Mapper());
    }

    private RowSyncAdapter(@NonNull Context context,
                           @NonNull FetchRowResponseV1Mapper fetchRowMapper) {
        super(context);
        this.fetchRowMapper = fetchRowMapper;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalChanges(@NonNull Account account) {
        return supplyAsync(() -> db.getRowDao().getLocallyDeletedRows(account.getId()), db.getParallelExecutor())
                .thenComposeAsync(rowsToDelete -> {
                    Log.v(TAG, "------ Pushing " + rowsToDelete.size() + " local row deletions for " + account.getAccountName());

                    return CompletableFuture.allOf(rowsToDelete.stream().map(row -> {
                        Log.i(TAG, "------ → DELETE: " + row.getRemoteId());
                        final var remoteId = row.getRemoteId();
                        if (remoteId == null) {
                            return runAsync(() -> db.getRowDao().delete(row), db.getSequentialExecutor());
                        } else {
                            return executeNetworkRequest(account, apis -> apis.apiV1().deleteRow(row.getRemoteId()))
                                    .thenComposeAsync(response -> {
                                        Log.i(TAG, "------ → HTTP " + response.code());
                                        if (response.isSuccessful()) {
                                            return runAsync(() -> db.getRowDao().delete(row), db.getSequentialExecutor());
                                        } else {
                                            serverErrorHandler.responseToException(response, "Could not delete row " + row.getRemoteId(), false).ifPresent(this::throwError);
                                            return CompletableFuture.completedFuture(null);
                                        }
                                    });
                        }
                    }).toArray(CompletableFuture[]::new));
                }, workExecutor)
                .thenApplyAsync(v -> db.getRowDao().getLocallyEditedRows(account.getId()), db.getParallelExecutor())
                .thenComposeAsync(fullRowsToUpdate -> {
                    Log.v(TAG, "------ Pushing " + fullRowsToUpdate.size() + " local row changes for " + account.getAccountName());

                    final var version = account.getTablesVersion();

                    if (version == null) {
                        throw new IllegalStateException(TablesVersion.class.getSimpleName() + " is null. Capabilities need to be synchronized before pushing local changes.");
                    }

                    return CompletableFuture.allOf(fullRowsToUpdate.stream().map(fullRow -> {
                        Log.i(TAG, "------ → PUT/POST: " + fullRow.getRow().getRemoteId());

                        if (fullRow.getRow().getRemoteId() == null) {

                            final var dataset = fetchRowMapper.toJsonElement(version, fullRow.getFullData());
                            return supplyAsync(() -> db.getTableDao().getRemoteId(fullRow.getRow().getTableId()), db.getParallelExecutor())
                                    .thenComposeAsync(tableRemoteId -> executeNetworkRequest(account, apis -> apis.apiV2().createRow(ENodeTypeV2Dto.TABLE, tableRemoteId, dataset)), workExecutor)
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
                                            return CompletableFuture.completedFuture(null);
                                        }
                                    });
                        } else {
                            final var dataset = fetchRowMapper.toJsonElement(version, fullRow.getFullData());

                            return executeNetworkRequest(account, apis -> apis.apiV1().updateRow(fullRow.getRow().getRemoteId(), dataset))
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
                                            return CompletableFuture.completedFuture(null);
                                        }
                                    });
                        }
                    }).toArray(CompletableFuture[]::new));
                }, workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account) {
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
                                    .thenComposeAsync(columnsAndIdMap -> {
                                        final var columns = columnsAndIdMap.first;
                                        final var columnRemoteAndLocalIds = columnsAndIdMap.second;
                                        final var target = new HashSet<FullRow>(columnRemoteAndLocalIds.values().size());

                                        return fetchRowsIntoTarget(account, table, 0, target, columns, columnRemoteAndLocalIds)
                                                .thenApplyAsync(v -> target, workExecutor);
                                    }, workExecutor)
                                    .thenApplyAsync(fetchedRows -> fetchedRows.stream()
                                            .map(FullRow::getRow)
                                            .map(AbstractRemoteEntity::getRemoteId)
                                            .collect(toUnmodifiableSet()), workExecutor)
                                    .thenApplyAsync(fetchedRowRemoteIds -> new Pair<>(fetchedRowRemoteIds, db.getRowDao().getRowRemoteAndLocalIds(table.getId())), db.getParallelExecutor())
                                    .thenApplyAsync(pair -> {
                                        final var fetchedRowRemoteIds = pair.first;
                                        final var existingRowIds = pair.second;

                                        Log.i(TAG, "------ ← Delete all rows except remoteId " + fetchedRowRemoteIds);
                                        for (final var remoteId : fetchedRowRemoteIds) {
                                            existingRowIds.remove(remoteId);
                                        }

                                        return existingRowIds;
                                    }, workExecutor)
                                    .thenApplyAsync(Map::values, workExecutor)
                                    .thenApplyAsync(HashSet::new, workExecutor)
                                    .thenAcceptAsync(existingRowIds -> existingRowIds.forEach(db.getRowDao()::delete), db.getSequentialExecutor())
                    ).toArray(CompletableFuture[]::new));
                });
    }


    @NonNull
    private CompletableFuture<Void> fetchRowsIntoTarget(@NonNull final Account account,
                                                        @NonNull final Table table,
                                                        final int offset,
                                                        @NonNull final Collection<FullRow> target,
                                                        @NonNull final Map<Long, Column> columns,
                                                        @NonNull final Map<Long, Long> columnRemoteAndLocalIds) {

        return runAsync(() -> Log.v(TAG, "------ Pulling remote rows for " + table.getTitle() + " (offset: " + offset + ")"), workExecutor)
                .thenComposeAsync(v -> this.getTableRemoteIdOrThrow(table, Row.class), workExecutor)
                .thenComposeAsync(tableRemoteId -> executeNetworkRequest(account, apis -> apis.apiV1().getRows(tableRemoteId, TablesV1API.DEFAULT_API_LIMIT_ROWS, offset)))
                .thenComposeAsync(response -> switch (response.code()) {
                    case 200: {
                        final var rowDtos = response.body();

                        if (rowDtos == null) {
                            throw new RuntimeException("Response body is null");
                        }

                        yield CompletableFuture.allOf(rowDtos.stream().map(rowDto -> supplyAsync(() -> {
                                    final var fullRow = fetchRowMapper.toEntity(account.getId(), rowDto, columns, Objects.requireNonNull(account.getTablesVersion()));

                                    final var row = fullRow.getRow();
                                    row.setAccountId(table.getAccountId());
                                    row.setTableId(table.getId());
                                    row.setETag(response.headers().get(HEADER_ETAG));

                                    return fullRow;
                                }, workExecutor)
                                        .thenApplyAsync(fullRow -> new Pair<>(fullRow, Optional
                                                .ofNullable(fullRow.getRow().getRemoteId())
                                                .map(remoteId -> db.getRowDao().getRowId(table.getId(), remoteId))
                                        ), db.getParallelExecutor())
                                        .thenComposeAsync(rowAndRowId -> upsertRow(rowAndRowId.first, rowAndRowId.second.orElse(null)), workExecutor)
                                        .thenApplyAsync(fullRow -> {
//                                    fullRow.getDataWithTypes()
//                                            .stream()
//                                            .map(DataWithType::getData)
//                                            .forEach(data -> data.setRowId(row.getId()));
//                                    final var columns = db.getColumnDao().getColumns(columnIds.values());
//
//                                    //FIXME rowID hard coded
//                                    final var fullRow = rowMapper.toEntity(account.getId(), 0L, rowDto, columns, account.getTablesVersion());
//                                    final var row = fullRow.getRow();
//
//                                    row.setAccountId(table.getAccountId());
//                                    row.setTableId(table.getId());
//                                    row.setETag(response.headers().get(HEADER_ETAG));

                                            return CompletableFuture.allOf(fullRow.getFullData().stream().map(fullData -> {
                                                        final var data = fullData.getData();
                                                        return supplyAsync(fullRow.getRow()::getId, workExecutor)
                                                                .thenAcceptAsync(data::setRowId, workExecutor)
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
                                                                        return CompletableFuture.completedFuture(null);
                                                                    }
                                                                }, workExecutor);
                                                    }).toArray(CompletableFuture[]::new))
                                                    .thenComposeAsync(v -> {
                                                        target.add(fullRow);
                                                        return CompletableFuture.<Void>completedFuture(null);
                                                    }, workExecutor);
                                        }, workExecutor)).toArray(CompletableFuture[]::new))
                                .thenCompose(v -> {
                                    if (rowDtos.size() != TablesV1API.DEFAULT_API_LIMIT_ROWS) {
                                        return CompletableFuture.completedFuture(null);
                                    }

                                    final var newOffset = offset + rowDtos.size();
                                    return fetchRowsIntoTarget(account, table, newOffset, target, columns, columnRemoteAndLocalIds);
                                });
                    }

                    default: {
                        final var future = new CompletableFuture<Void>();
                        serverErrorHandler
                                .responseToException(response, "Could not fetch rows for table with remote ID " + table.getRemoteId(), true)
                                .ifPresentOrElse(
                                        future::completeExceptionally,
                                        () -> future.complete(null));
                        yield future;
                    }
                });
    }

    @NonNull
    public CompletableFuture<FullRow> upsertRow(@NonNull final FullRow fullRow,
                                                @Nullable Long rowId) {
        return supplyAsync(() -> {
            final var row = fullRow.getRow();

            if (rowId == null) {
                Log.i(TAG, "------ ← Adding row " + row.getRemoteId() + " to database");
                return supplyAsync(() -> db.getRowDao().insert(row), db.getSequentialExecutor())
                        .thenAcceptAsync(row::setId, workExecutor);

            } else {
                Log.i(TAG, "------ ← Updating row " + row.getRemoteId() + " in database");
                row.setId(rowId);
                return runAsync(() -> db.getRowDao().update(row), db.getSequentialExecutor());
            }
        }, workExecutor)
                .thenApplyAsync(v -> fullRow, workExecutor);
    }
}