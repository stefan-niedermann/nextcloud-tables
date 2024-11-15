package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnRequestV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.DataTypeCreatorServiceRegistry;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.ColumnRequestV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.ColumnV2Mapper;

class ColumnSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = ColumnSyncAdapter.class.getSimpleName();
    private final DataTypeServiceRegistry<ColumnCreator> columnCreator;
    private final Mapper<ColumnV2Dto, FullColumn> columnRequestMapper;
    private final Function<FullColumn, ColumnRequestV1Dto> columnRequestV1Mapper;

    public ColumnSyncAdapter(@NonNull Context context) {
        super(context);
        this.columnCreator = new DataTypeCreatorServiceRegistry();
        this.columnRequestMapper = new ColumnV2Mapper();
        this.columnRequestV1Mapper = new ColumnRequestV1Mapper();
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalChanges(@NonNull Account account) {
        Log.v(TAG, "--- Pushing local columns for " + account.getAccountName());
        return supplyAsync(() -> db.getColumnDao().getFullColumns(account.getId(), DBStatus.LOCAL_DELETED), db.getParallelExecutor())
                .thenAcceptAsync(columnsToDelete -> CompletableFuture.allOf(columnsToDelete.stream().map(fullColumn -> {
                    final var column = fullColumn.getColumn();
                    Log.i(TAG, "--- → DELETE: " + column.getTitle());
                    final var remoteId = column.getRemoteId();
                    if (remoteId == null) {
                        return supplyAsync(() -> {
                            db.getColumnDao().delete(column);
                            return null;
                        }, db.getSequentialExecutor());
                    } else {
                        return executeNetworkRequest(account, apis -> apis.apiV1().deleteColumn(column.getRemoteId()))
                                .thenComposeAsync(response -> {
                                    Log.i(TAG, "--- → HTTP " + response.code());
                                    if (response.isSuccessful()) {
                                        return supplyAsync(() -> {
                                            db.getColumnDao().delete(column);
                                            return null;
                                        }, db.getSequentialExecutor());
                                    } else {
                                        serverErrorHandler.responseToException(response, "Could not delete column " + column.getTitle(), true).ifPresent(this::throwError);
                                        return CompletableFuture.completedFuture(null);
                                    }
                                });
                    }
                }).toArray(CompletableFuture[]::new)), workExecutor)
                .thenApplyAsync(v -> db.getColumnDao().getFullColumns(account.getId(), DBStatus.LOCAL_EDITED), db.getParallelExecutor())
                .thenAcceptAsync(columnsToUpdate -> CompletableFuture.allOf(columnsToUpdate.stream().map(fullColumn -> {
                    final var column = fullColumn.getColumn();

                    Log.i(TAG, "--- → PUT/POST: " + column.getTitle());
                    if (column.getRemoteId() == null) {
                        final var columnDto = columnRequestMapper.toDto(fullColumn);
                        return executeNetworkRequest(account, apis -> columnCreator.getService(column.getDataType())
                                .createColumn(apis.apiV2(), db.getTableDao().getRemoteId(column.getTableId()), columnDto))
                                .thenApplyAsync(response -> {
                                    Log.i(TAG, "--- → HTTP " + response.code());
                                    if (response.isSuccessful()) {
                                        column.setStatus(DBStatus.VOID);
                                        final var body = response.body();

                                        if (body == null || body.ocs == null || body.ocs.data == null) {
                                            throw new NullPointerException("Pushing changes for column " + column.getTitle() + " was successful, but response body was empty");
                                        }

                                        column.setRemoteId(body.ocs.data.remoteId());
                                        return supplyAsync(() -> {
                                            db.getColumnDao().update(column);
                                            return null;
                                        }, db.getSequentialExecutor());
                                    } else {
                                        serverErrorHandler.responseToException(response, "Could not push local changes for column " + column.getTitle(), true).ifPresent(this::throwError);
                                        return CompletableFuture.completedFuture(null);
                                    }
                                }, workExecutor);
                    } else {
                        final var columnDtos = columnRequestV1Mapper.apply(fullColumn);
                        return this.executeNetworkRequest(account, apis -> apis.apiV1().updateColumn(column.getRemoteId(), columnDtos))
                                .thenApplyAsync(response -> {
                                    Log.i(TAG, "--- → HTTP " + response.code());
                                    if (response.isSuccessful()) {
                                        column.setStatus(DBStatus.VOID);
                                        final var body = response.body();

                                        if (body == null) {
                                            throw new NullPointerException("Pushing changes for column " + column.getTitle() + " was successful, but response body was empty");
                                        }

                                        column.setRemoteId(body.remoteId());
                                        return supplyAsync(() -> {
                                            db.getColumnDao().update(column);
                                            return null;
                                        }, db.getSequentialExecutor());
                                    } else {
                                        serverErrorHandler.responseToException(response, "Could not push local changes for column " + column.getTitle(), true).ifPresent(this::throwError);
                                        return CompletableFuture.completedFuture(null);
                                    }

                                }, workExecutor);
                    }
                }).toArray(CompletableFuture[]::new)), workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account) {
        return supplyAsync(() -> db.getTableDao().getTables(account.getId()), db.getParallelExecutor())
                .thenAcceptAsync(tables -> CompletableFuture.allOf(tables.stream().map(table ->
                        this.getTableRemoteIdOrThrow(table, Column.class)
                                .thenComposeAsync(tableRemoteId -> executeNetworkRequest(account, apis -> apis.apiV2().getColumns(ENodeTypeV2Dto.TABLE, tableRemoteId)), workExecutor)
                                .thenComposeAsync(response -> switch (response.code()) {
                                    case 200 -> {
                                        final var responseBody = response.body();

                                        if (responseBody == null || responseBody.ocs == null || responseBody.ocs.data == null) {
                                            throw new RuntimeException("Response body is null");
                                        }

                                        final var columnDtos = responseBody.ocs.data;
                                        final var columnRemoteIds = columnDtos.stream().map(ColumnV2Dto::remoteId).collect(toUnmodifiableSet());

                                        yield supplyAsync(() -> db.getColumnDao().getColumnRemoteAndLocalIds(table.getId(), columnRemoteIds), db.getParallelExecutor())
                                                .thenAcceptAsync(columnIds -> CompletableFuture.allOf(columnDtos.stream().map(columnDto -> {
                                                    final var fullColumn = columnRequestMapper.toEntity(columnDto);
                                                    final var column = fullColumn.getColumn();
                                                    column.setAccountId(account.getId());
                                                    column.setTableId(table.getId());
                                                    column.setETag(response.headers().get(HEADER_ETAG));

                                                    final CompletableFuture<?> columnUpdateFuture;
                                                    final var columnId = columnIds.get(column.getRemoteId());
                                                    if (columnId == null) {
                                                        Log.i(TAG, "--- ← Adding column " + column.getTitle() + " to database");
                                                        columnUpdateFuture = supplyAsync(() -> db.getColumnDao().insert(column), db.getSequentialExecutor())
                                                                .thenAcceptAsync(column::setId, workExecutor);
                                                    } else {
                                                        column.setId(columnId);
                                                        Log.i(TAG, "--- ← Updating column " + column.getTitle() + " in database");
                                                        columnUpdateFuture = supplyAsync(() -> {
                                                            db.getColumnDao().update(column);
                                                            return null;
                                                        }, db.getSequentialExecutor());
                                                    }

                                                    final var selectionOptions = fullColumn.getSelectionOptions();
                                                    final var selectionOptionRemoteIds = selectionOptions.stream().map(SelectionOption::getRemoteId).collect(toUnmodifiableSet());
                                                    return columnUpdateFuture.thenApplyAsync(v -> db.getSelectionOptionDao().getSelectionOptionRemoteAndLocalIds(column.getId(), selectionOptionRemoteIds), db.getParallelExecutor())
                                                            .thenApplyAsync(selectionOptionIds -> CompletableFuture.allOf(selectionOptions.stream().map(selectionOption -> {
                                                                selectionOption.setColumnId(column.getId());
                                                                selectionOption.setAccountId(column.getAccountId());

                                                                final var selectionOptionId = selectionOptionIds.get(selectionOption.getRemoteId());
                                                                if (selectionOptionId == null) {
                                                                    Log.i(TAG, "--- ← Adding selection option " + selectionOption.getLabel() + " to database");
                                                                    return supplyAsync(() -> db.getSelectionOptionDao().insert(selectionOption), db.getSequentialExecutor())
                                                                            .thenAcceptAsync(selectionOption::setId, workExecutor);

                                                                } else {
                                                                    selectionOption.setId(selectionOptionId);
                                                                    Log.i(TAG, "--- ← Updating selection option " + selectionOption.getLabel() + " in database");
                                                                    return supplyAsync(() -> {
                                                                        db.getSelectionOptionDao().update(selectionOption);
                                                                        return null;
                                                                    }, db.getSequentialExecutor());
                                                                }
                                                            }).toArray(CompletableFuture[]::new)), workExecutor)
                                                            .thenAcceptAsync(v -> Log.i(TAG, "--- ← Delete all selection options except remoteId " + selectionOptionRemoteIds), workExecutor)
                                                            .thenAcceptAsync(v -> db.getSelectionOptionDao().deleteExcept(table.getId(), selectionOptionRemoteIds), db.getSequentialExecutor());
                                                }).toArray(CompletableFuture[]::new)), workExecutor)
                                                .thenAcceptAsync(v -> Log.i(TAG, "--- ← Delete all columns except remoteId " + columnRemoteIds), workExecutor)
                                                .thenAcceptAsync(v -> db.getColumnDao().deleteExcept(table.getId(), columnRemoteIds));
                                    }
                                    default -> {
                                        serverErrorHandler.responseToException(response, "At table remote ID: " + table.getRemoteId(), true).ifPresent(this::throwError);
                                        yield CompletableFuture.completedFuture(null);
                                    }
                                })
                ).toArray(CompletableFuture[]::new)), workExecutor);
    }
}
