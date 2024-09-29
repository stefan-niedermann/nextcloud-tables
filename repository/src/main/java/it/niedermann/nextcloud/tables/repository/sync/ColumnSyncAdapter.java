package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UpdateColumnV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.UpdateColumnV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.ColumnV2Mapper;
import it.niedermann.nextcloud.tables.types.EDataType;

public class ColumnSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = ColumnSyncAdapter.class.getSimpleName();
    private final Mapper<ColumnV2Dto, Column> columnMapper;
    private final Mapper<UpdateColumnV1Dto, Column> updateColumnMapper;

    public ColumnSyncAdapter(@NonNull TablesDatabase db,
                             @NonNull Context context) {
        super(db, context);
        this.columnMapper = new ColumnV2Mapper();
        this.updateColumnMapper = new UpdateColumnV1Mapper();
    }

    @Override
    public void pushLocalChanges(@NonNull TablesV2API apiV2,
                                 @NonNull TablesV1API apiV1,
                                 @NonNull Account account) throws Exception {
        Log.v(TAG, "--- Pushing local columns for " + account.getAccountName());
        final var columnsToDelete = db.getColumnDao().getColumns(account.getId(), DBStatus.LOCAL_DELETED);
        for (final var column : columnsToDelete) {
            Log.i(TAG, "--- → DELETE: " + column.getTitle());
            final var remoteId = column.getRemoteId();
            if (remoteId == null) {
                db.getColumnDao().delete(column);
            } else {
                final var response = apiV1.deleteColumn(column.getRemoteId()).execute();
                Log.i(TAG, "--- → HTTP " + response.code());
                if (response.isSuccessful()) {
                    db.getColumnDao().delete(column);
                } else {
                    serverErrorHandler.handle(response, "Could not delete column " + column.getTitle());
                }
            }
        }

        final var columnsToUpdate = db.getColumnDao().getColumns(account.getId(), DBStatus.LOCAL_EDITED);
        for (final var column : columnsToUpdate) {
            // TODO maybe this can be queried only once using MultiMap
            column.setSelectionOptions(db.getSelectionOptionDao().getSelectionOptions(column.getId()));

            Log.i(TAG, "--- → PUT/POST: " + column.getTitle());
            if (column.getRemoteId() == null) {
                final var response = EDataType
                        .findByColumn(column)
                        .createColumn(apiV2, db.getTableDao().getRemoteId(column.getTableId()), columnMapper.toDto(column)).execute();

                Log.i(TAG, "--- → HTTP " + response.code());
                if (response.isSuccessful()) {
                    column.setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null || body.ocs == null || body.ocs.data == null) {
                        throw new NullPointerException("Pushing changes for column " + column.getTitle() + " was successfull, but response body was empty");
                    }

                    column.setRemoteId(body.ocs.data.remoteId());
                    db.getColumnDao().update(column);
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for column " + column.getTitle());
                }
            } else {
                final var response = apiV1.updateColumn(
                                column.getRemoteId(),
                                updateColumnMapper.toDto(column))
                        .execute();

                Log.i(TAG, "--- → HTTP " + response.code());
                if (response.isSuccessful()) {
                    column.setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null) {
                        throw new NullPointerException("Pushing changes for column " + column.getTitle() + " was successfull, but response body was empty");
                    }

                    column.setRemoteId(body.remoteId());
                    db.getColumnDao().update(column);
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for column " + column.getTitle());
                }
            }
        }
    }

    @Override
    public void pullRemoteChanges(@NonNull TablesV2API apiV2,
                                  @NonNull TablesV1API apiV1,
                                  @NonNull Account account) throws Exception {
        for (final var table : db.getTableDao().getTables(account.getId())) {
            final var tableRemoteId = table.getRemoteId();
            if (tableRemoteId == null) {
                throw new IllegalStateException("Expected table remote ID to be present when pushing column changes, but was null");
            }

            final var request = apiV2.getColumns(ENodeTypeV2Dto.TABLE, tableRemoteId);
            final var response = request.execute();
            //noinspection SwitchStatementWithTooFewBranches
            switch (response.code()) {
                case 200: {
                    final var responseBody = response.body();
                    if (responseBody == null || responseBody.ocs == null || responseBody.ocs.data == null) {
                        throw new RuntimeException("Response body is null");
                    }

                    final var columnDtos = responseBody.ocs.data;

                    final var columnRemoteIds = columnDtos.stream().map(ColumnV2Dto::remoteId).collect(toUnmodifiableSet());
                    final var columnIds = db.getColumnDao().getColumnRemoteAndLocalIds(account.getId(), columnRemoteIds);

                    for (final var columnDto : columnDtos) {
                        final var column = columnMapper.toEntity(columnDto);
                        column.setAccountId(account.getId());
                        column.setTableId(table.getId());
                        column.setETag(response.headers().get(HEADER_ETAG));

                        final var columnId = columnIds.get(column.getRemoteId());
                        if (columnId == null) {
                            Log.i(TAG, "--- ← Adding column " + column.getTitle() + " to database");
                            column.setId(db.getColumnDao().insert(column));
                        } else {
                            column.setId(columnId);
                            Log.i(TAG, "--- ← Updating column " + column.getTitle() + " in database");
                            db.getColumnDao().update(column);
                        }

                        final var selectionOptions = column.getSelectionOptions();

                        final var selectionOptionRemoteIds = selectionOptions.stream().map(AbstractRemoteEntity::getRemoteId).collect(toUnmodifiableSet());
                        final var selectionOptionIds = db.getSelectionOptionDao().getSelectionOptionRemoteAndLocalIds(column.getId(), selectionOptionRemoteIds);

                        for (final var selectionOption : selectionOptions) {
                            selectionOption.setColumnId(column.getId());
                            selectionOption.setAccountId(column.getAccountId());

                            final var selectionOptionId = selectionOptionIds.get(selectionOption.getRemoteId());
                            if (selectionOptionId == null) {
                                Log.i(TAG, "--- ← Adding selection option " + selectionOption.getLabel() + " to database");
                                db.getSelectionOptionDao().insert(selectionOption);
                            } else {
                                selectionOption.setId(selectionOptionId);
                                Log.i(TAG, "--- ← Updating selection option " + selectionOption.getLabel() + " in database");
                                db.getSelectionOptionDao().update(selectionOption);
                            }
                        }

                        Log.i(TAG, "--- ← Delete all selection options except remoteId " + selectionOptionRemoteIds);
                        db.getSelectionOptionDao().deleteExcept(table.getId(), selectionOptionRemoteIds);
                    }

                    Log.i(TAG, "--- ← Delete all columns except remoteId " + columnRemoteIds);
                    db.getColumnDao().deleteExcept(table.getId(), columnRemoteIds);
                    break;
                }

                default: {
                    serverErrorHandler.handle(response, "At table remote ID: " + table.getRemoteId());
                }
            }
        }
    }
}
