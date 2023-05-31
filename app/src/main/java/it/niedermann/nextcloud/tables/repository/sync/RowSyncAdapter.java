package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.remote.adapter.DataAdapter;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;

public class RowSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = RowSyncAdapter.class.getSimpleName();
    private final DataAdapter dataAdapter;
    private final ExecutorService rowFetchExecutor;

    public RowSyncAdapter(@NonNull TablesDatabase db, @NonNull Context context) {
        this(db, new DataAdapter(), context, Executors.newCachedThreadPool());
    }

    private RowSyncAdapter(@NonNull TablesDatabase db,
                           @NonNull DataAdapter dataAdapter,
                           @NonNull Context context,
                           @NonNull ExecutorService rowFetchExecutor) {
        super(db, context);
        this.dataAdapter = dataAdapter;
        this.rowFetchExecutor = rowFetchExecutor;
    }

    @Override
    public void pushLocalChanges(@NonNull TablesAPI api, @NonNull Account account) throws Exception {
        final var rowsToDelete = db.getRowDao().getLocallyDeletedRows(account.getId());
        Log.v(TAG, "------ Pushing " + rowsToDelete.size() + " local row deletions for " + account.getAccountName());
        for (final var row : rowsToDelete) {
            Log.i(TAG, "------ → DELETE: " + row.getRemoteId());
            final var remoteId = row.getRemoteId();
            if (remoteId == null) {
                db.getRowDao().delete(row);
            } else {
                final var response = api.deleteRow(row.getRemoteId()).execute();
                Log.i(TAG, "------ → HTTP " + response.code());
                if (response.isSuccessful()) {
                    db.getRowDao().delete(row);
                } else {
                    serverErrorHandler.handle(response, "Could not delete row " + row.getRemoteId());
                }
            }
        }

        final var rowsToUpdate = db.getRowDao().getLocallyEditedRows(account.getId());
        Log.v(TAG, "------ Pushing " + rowsToDelete.size() + " local row changes for " + account.getAccountName());

        for (final var row : rowsToUpdate) {
            Log.i(TAG, "------ → PUT/POST: " + row.getRemoteId());
            final var dataset = db.getDataDao().getDataForRow(row.getId());
            final var columns = db.getColumnDao().getColumns(Arrays.stream(dataset).map(Data::getColumnId).collect(toUnmodifiableSet()));

            row.setData(dataset);

            final var response = row.getRemoteId() == null
                    // TODO perf: fetch all remoteIds at once
                    ? api.createRow(db.getTableDao().getRemoteId(row.getTableId()), dataAdapter.serialize(columns, row.getData())).execute()
                    : api.updateRow(row.getRemoteId(), dataAdapter.serialize(columns, row.getData())).execute();
            Log.i(TAG, "------ → HTTP " + response.code());
            if (response.isSuccessful()) {
                row.setStatus(DBStatus.VOID);
                final var body = response.body();
                if (body == null) {
                    throw new NullPointerException("Pushing changes for row " + row.getRemoteId() + " was successfully, but response body was empty");
                }

                row.setRemoteId(body.getRemoteId());
                db.getRowDao().update(row);
            } else {
                serverErrorHandler.handle(response, "Could not push local changes for row " + row.getRemoteId());
            }
        }
    }

    @Override
    public void pullRemoteChanges(@NonNull TablesAPI api, @NonNull Account account) throws Exception {
        final var tables = db.getTableDao().getTablesWithReadPermission(account.getId());
        final var latch = new CountDownLatch(tables.size());
        final var exceptions = new LinkedList<Exception>();

        for (final var table : tables) {
            rowFetchExecutor.submit(() -> {
                try {
                    final var fetchedRows = new HashSet<Row>();
                    int offset = 0;

                    fetchRowsLoop:
                    while (true) {
                        Log.v(TAG, "------ Pulling remote rows for " + table.getTitle() + " (offset: " + offset + ")");
                        final var tableRemoteId = table.getRemoteId();
                        if (tableRemoteId == null) {
                            throw new IllegalStateException("Expected table remote ID to be present when pushing row changes, but was null");
                        }

                        final var request = api.getRows(tableRemoteId, TablesAPI.DEFAULT_API_LIMIT_ROWS, offset);
                        final var response = request.execute();
                        //noinspection SwitchStatementWithTooFewBranches
                        switch (response.code()) {
                            case 200: {
                                final var rows = response.body();
                                if (rows == null) {
                                    throw new RuntimeException("Response body is null");
                                }

                                for (final var row : rows) {
                                    row.setAccountId(table.getAccountId());
                                    row.setTableId(table.getId());
                                    row.setETag(response.headers().get(HEADER_ETAG));
                                }

                                fetchedRows.addAll(rows);

                                if (rows.size() != TablesAPI.DEFAULT_API_LIMIT_ROWS) {
                                    break fetchRowsLoop;
                                }

                                offset += rows.size();

                                break;
                            }

                            default: {
                                serverErrorHandler.handle(response, "Could not fetch rows for table with remote ID " + table.getRemoteId());
                            }
                        }
                    }

                    final var rowRemoteIds = fetchedRows.stream().map(AbstractRemoteEntity::getRemoteId).collect(toUnmodifiableSet());
                    final var rowIds = db.getRowDao().getRowRemoteAndLocalIds(table.getAccountId(), rowRemoteIds);

                    for (final var row : fetchedRows) {
                        final var rowId = rowIds.get(row.getRemoteId());
                        if (rowId == null) {
                            Log.i(TAG, "------ ← Adding " + table.getTitle() + " to database");
                            row.setId(db.getRowDao().insert(row));
                        } else {
                            row.setId(rowId);
                            Log.i(TAG, "------ ← Updating row " + row.getRemoteId() + " in database");
                            db.getRowDao().update(row);
                        }

                        final var columnRemoteIds = Arrays.stream(row.getData()).map(Data::getRemoteColumnId).collect(toUnmodifiableSet());
                        final var columnIds = db.getColumnDao().getColumnRemoteAndLocalIds(table.getAccountId(), columnRemoteIds);
                        final var columns = db.getColumnDao().getColumns(columnIds.values());

                        for (final var data : row.getData()) {
                            final var columnId = columnIds.get(data.getRemoteColumnId());
                            if (columnId == null) {
                                Log.w(TAG, "------ Could not find remoteColumnId " + data.getRemoteColumnId() + ". Probably this column has been deleted but its data is still being responded by the server (See https://github.com/nextcloud/tables/issues/257)");
                            } else {
                                data.setAccountId(table.getAccountId());
                                data.setRowId(row.getId());
                                data.setColumnId(columnId);

                                final var type = dataAdapter.getTypeForData(columns, data);
                                data.setValue(dataAdapter.deserialize(type, data.getValue()));

                                final var existingData = db.getDataDao().getDataForCoordinates(data.getColumnId(), data.getRowId());
                                if (existingData == null) {
                                    db.getDataDao().insert(data);
                                } else {
                                    data.setId(existingData.getId());
                                    db.getDataDao().update(data);
                                }

                                // Data deletion is handled by database constraints
                            }
                        }
                    }

                    Log.i(TAG, "------ ← Delete all rows except remoteId " + rowRemoteIds);
                    db.getRowDao().deleteExcept(table.getId(), rowRemoteIds);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        if (exceptions.size() > 0) {
            // TODO we can only throw one exception, do we need a custom type with a list or is the user supposed to handle them one by one?
            for (final var exception : exceptions) {
                exception.printStackTrace();
            }
            throw exceptions.get(0);
        }
    }
}
