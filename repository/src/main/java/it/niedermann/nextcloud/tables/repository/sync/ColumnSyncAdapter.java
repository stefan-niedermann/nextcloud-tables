package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.adapter.ColumnAdapter;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import it.niedermann.nextcloud.tables.remote.api.TablesV1API;
import it.niedermann.nextcloud.tables.remote.model.ENodeType;
import it.niedermann.nextcloud.tables.types.EDataType;

public class ColumnSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = ColumnSyncAdapter.class.getSimpleName();
    private final ColumnAdapter columnAdapter;

    public ColumnSyncAdapter(@NonNull TablesDatabase db, @NonNull Context context) {
        super(db, context);
        this.columnAdapter = new ColumnAdapter();
    }

    @Override
    public void pushLocalChanges(@NonNull TablesAPI api,
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
                        .createColumn(api, db.getTableDao().getRemoteId(column.getTableId()), column).execute();

                Log.i(TAG, "--- → HTTP " + response.code());
                if (response.isSuccessful()) {
                    column.setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null || body.ocs == null || body.ocs.data == null) {
                        throw new NullPointerException("Pushing changes for column " + column.getTitle() + " was successfull, but response body was empty");
                    }

                    column.setRemoteId(body.ocs.data.getRemoteId());
                    db.getColumnDao().update(column);
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for column " + column.getTitle());
                }
            } else {
                final var response = apiV1.updateColumn(column.getRemoteId(),
                        column.getTitle(),
                        this.columnAdapter.serializeMandatory(column.isMandatory()),
                        column.getDescription(),
                        column.getNumberPrefix(),
                        column.getNumberSuffix(),
                        column.getNumberDefault(),
                        column.getNumberMin(),
                        column.getNumberMax(),
                        column.getNumberDecimals(),
                        column.getTextDefault(),
                        column.getTextAllowedPattern(),
                        column.getTextMaxLength(),
                        column.getSelectionOptions(),
//                    this.columnAdapter.serializeSelectionOptions(column.getSelectionOptions()),
                        this.columnAdapter.serializeSelectionDefault(column.getSelectionDefault()),
                        column.getDatetimeDefault()).execute();

                Log.i(TAG, "--- → HTTP " + response.code());
                if (response.isSuccessful()) {
                    column.setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null) {
                        throw new NullPointerException("Pushing changes for column " + column.getTitle() + " was successfull, but response body was empty");
                    }

                    column.setRemoteId(body.getRemoteId());
                    db.getColumnDao().update(column);
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for column " + column.getTitle());
                }
            }
        }
    }

    @Override
    public void pullRemoteChanges(@NonNull TablesAPI api,
                                  @NonNull TablesV1API apiV1,
                                  @NonNull Account account) throws Exception {
        for (final var table : db.getTableDao().getTables(account.getId())) {
            final var tableRemoteId = table.getRemoteId();
            if (tableRemoteId == null) {
                throw new IllegalStateException("Expected table remote ID to be present when pushing column changes, but was null");
            }

            final var request = api.getColumns(ENodeType.TABLE, tableRemoteId);
            final var response = request.execute();
            //noinspection SwitchStatementWithTooFewBranches
            switch (response.code()) {
                case 200: {
                    final var responseBody = response.body();
                    if (responseBody == null || responseBody.ocs == null || responseBody.ocs.data == null) {
                        throw new RuntimeException("Response body is null");
                    }

                    final var columns = responseBody.ocs.data;

                    final var columnRemoteIds = columns.stream().map(AbstractRemoteEntity::getRemoteId).collect(toUnmodifiableSet());
                    final var columnIds = db.getColumnDao().getColumnRemoteAndLocalIds(account.getId(), columnRemoteIds);

                    for (final var column : columns) {
                        column.setAccountId(account.getId());
                        column.setTableId(table.getId());
                        column.setETag(response.headers().get(HEADER_ETAG));
                        column.setSelectionDefault(columnAdapter.deserializeSelectionDefault(column.getSelectionDefault()));

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
