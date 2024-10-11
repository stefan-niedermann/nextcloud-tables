package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.function.Function;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnRequestV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.DataTypeCreatorServiceRegistry;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.ColumnRequestV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.ColumnV2Mapper;

public class ColumnSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = ColumnSyncAdapter.class.getSimpleName();
    private final DataTypeServiceRegistry<ColumnCreator> columnCreator;
    private final Mapper<ColumnV2Dto, FullColumn> columnRequestMapper;
    private final Function<FullColumn, ColumnRequestV1Dto> columnRequestV1Mapper;

    public ColumnSyncAdapter(@NonNull TablesDatabase db,
                             @NonNull Context context) {
        super(db, context);
        this.columnCreator = new DataTypeCreatorServiceRegistry();
        this.columnRequestMapper = new ColumnV2Mapper();
        this.columnRequestV1Mapper = new ColumnRequestV1Mapper();
    }

    @Override
    public void pushLocalChanges(@NonNull TablesV2API apiV2,
                                 @NonNull TablesV1API apiV1,
                                 @NonNull Account account) throws Exception {
        Log.v(TAG, "--- Pushing local columns for " + account.getAccountName());
        final var columnsToDelete = db.getColumnDao().getFullColumns(account.getId(), DBStatus.LOCAL_DELETED);
        for (final var fullColumn : columnsToDelete) {
            final var column = fullColumn.getColumn();
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

        final var columnsToUpdate = db.getColumnDao().getFullColumns(account.getId(), DBStatus.LOCAL_EDITED);
        for (final var fullColumn : columnsToUpdate) {
            final var column = fullColumn.getColumn();

            Log.i(TAG, "--- → PUT/POST: " + column.getTitle());
            if (column.getRemoteId() == null) {
                final var response = columnCreator.getService(column.getDataType())
                        .createColumn(apiV2, db.getTableDao().getRemoteId(column.getTableId()), columnRequestMapper.toDto(fullColumn)).execute();

                Log.i(TAG, "--- → HTTP " + response.code());
                if (response.isSuccessful()) {
                    column.setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null || body.ocs == null || body.ocs.data == null) {
                        throw new NullPointerException("Pushing changes for column " + column.getTitle() + " was successful, but response body was empty");
                    }

                    column.setRemoteId(body.ocs.data.remoteId());
                    db.getColumnDao().update(column);
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for column " + column.getTitle());
                }
            } else {
                final var response = apiV1.updateColumn(
                                column.getRemoteId(),
                                columnRequestV1Mapper.apply(fullColumn))
                        .execute();

                Log.i(TAG, "--- → HTTP " + response.code());
                if (response.isSuccessful()) {
                    column.setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null) {
                        throw new NullPointerException("Pushing changes for column " + column.getTitle() + " was successful, but response body was empty");
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
                    final var columnIds = db.getColumnDao().getColumnRemoteAndLocalIds(table.getId(), columnRemoteIds);

                    for (final var columnDto : columnDtos) {
                        final var fullColumn = columnRequestMapper.toEntity(columnDto);
                        final var column = fullColumn.getColumn();
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

                        final var selectionOptions = fullColumn.getSelectionOptions();
                        final var selectionOptionRemoteIds = selectionOptions.stream().map(SelectionOption::getRemoteId).collect(toUnmodifiableSet());
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
