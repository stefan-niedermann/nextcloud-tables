package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.BuildConfig;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.RowV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.RowV1Mapper;
import it.niedermann.nextcloud.tables.types.EDataType;

public class RowSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = RowSyncAdapter.class.getSimpleName();
    private final ExecutorService rowFetchExecutor;
    private final Mapper<RowV1Dto, Row> rowMapper;

    public RowSyncAdapter(@NonNull TablesDatabase db, @NonNull Context context) {
        this(db, context, Executors.newCachedThreadPool(), new RowV1Mapper());
    }

    private RowSyncAdapter(@NonNull TablesDatabase db,
                           @NonNull Context context,
                           @NonNull ExecutorService rowFetchExecutor,
                           @NonNull Mapper<RowV1Dto, Row> rowMapper) {
        super(db, context);
        this.rowFetchExecutor = rowFetchExecutor;
        this.rowMapper = rowMapper;
    }

    @Override
    public void pushLocalChanges(@NonNull TablesV2API apiV2,
                                 @NonNull TablesV1API apiV1,
                                 @NonNull Account account) throws Exception {
        final var version = account.getTablesVersion();
        if (version == null) {
            throw new IllegalStateException(TablesVersion.class.getSimpleName() + " is null. Capabilities need to be synchronized before pushing local changes.");
        }

        final var rowsToDelete = db.getRowDao().getLocallyDeletedRows(account.getId());
        Log.v(TAG, "------ Pushing " + rowsToDelete.size() + " local row deletions for " + account.getAccountName());
        for (final var row : rowsToDelete) {
            Log.i(TAG, "------ → DELETE: " + row.getRemoteId());
            final var remoteId = row.getRemoteId();
            if (remoteId == null) {
                db.getRowDao().delete(row);
            } else {
                final var response = apiV1.deleteRow(row.getRemoteId()).execute();
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

            if (row.getRemoteId() == null) {
                final var response = apiV2.createRow(
                        ENodeTypeV2Dto.TABLE,
                        db.getTableDao().getRemoteId(row.getTableId()),
                        serialize(columns, version, row.getData())).execute();

                if (response.isSuccessful()) {
                    row.setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null || body.ocs == null || body.ocs.data == null) {
                        throw new NullPointerException("Pushing changes for row " + row.getRemoteId() + " was successfully, but response body was empty");
                    }

                    row.setRemoteId(body.ocs.data.remoteId());
                    db.getRowDao().update(row);
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for row " + row.getRemoteId());
                }
            } else {
                final var response = apiV1.updateRow(row.getRemoteId(), serialize(columns, version, row.getData())).execute();
                Log.i(TAG, "------ → HTTP " + response.code());
                if (response.isSuccessful()) {
                    row.setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null) {
                        throw new NullPointerException("Pushing changes for row " + row.getRemoteId() + " was successfully, but response body was empty");
                    }

                    row.setRemoteId(body.remoteId());
                    db.getRowDao().update(row);
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for row " + row.getRemoteId());
                }
            }
        }
    }

    @Override
    public void pullRemoteChanges(@NonNull TablesV2API api,
                                  @NonNull TablesV1API apiV1,
                                  @NonNull Account account) throws Exception {
        final var tables = db.getTableDao().getTablesWithReadPermission(account.getId());
        final var latch = new CountDownLatch(tables.size());
        final var exceptions = new LinkedList<Exception>();
        final var version = account.getTablesVersion();
        if (version == null) {
            throw new IllegalStateException(TablesVersion.class.getSimpleName() + " is null. Capabilities need to be synchronized before pulling remote changes.");
        }

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

                        final var request = apiV1.getRows(tableRemoteId, TablesV1API.DEFAULT_API_LIMIT_ROWS, offset);
                        final var response = request.execute();
                        //noinspection SwitchStatementWithTooFewBranches
                        switch (response.code()) {
                            case 200: {
                                final var rowDtos = response.body();
                                if (rowDtos == null) {
                                    throw new RuntimeException("Response body is null");
                                }

                                for (final var rowDto : rowDtos) {
                                    final var row = rowMapper.toEntity(rowDto);
                                    row.setAccountId(table.getAccountId());
                                    row.setTableId(table.getId());
                                    row.setETag(response.headers().get(HEADER_ETAG));
                                    fetchedRows.add(row);
                                }

                                if (rowDtos.size() != TablesV1API.DEFAULT_API_LIMIT_ROWS) {
                                    break fetchRowsLoop;
                                }

                                offset += rowDtos.size();

                                break;
                            }

                            default: {
                                serverErrorHandler.handle(response, "Could not fetch rows for table with remote ID " + table.getRemoteId());
                            }
                        }
                    }

                    final var fetchedRowRemoteIds = fetchedRows.stream().map(AbstractRemoteEntity::getRemoteId).collect(toUnmodifiableSet());
                    final var rowIds = db.getRowDao().getRowRemoteAndLocalIds(table.getId());

                    for (final var row : fetchedRows) {
                        final var rowId = rowIds.get(row.getRemoteId());
                        if (rowId == null) {
                            Log.i(TAG, "------ ← Adding table " + table.getTitle() + " to database");
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

                                final var type = getTypeForData(columns, data);
                                data.setValue(interceptResponse(type, version, data.getValue()));

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

                    Log.i(TAG, "------ ← Delete all rows except remoteId " + fetchedRowRemoteIds);
                    final var existingRowIds = db.getRowDao().getRowRemoteAndLocalIds(table.getId());

                    for (final var remoteId : fetchedRowRemoteIds) {
                        existingRowIds.remove(remoteId);
                    }

                    for (final var id : new HashSet<>(existingRowIds.values())) {
                        db.getRowDao().delete(id);
                    }
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        if (!exceptions.isEmpty()) {
            // TODO we can only throw one exception, do we need a custom type with a list or is the user supposed to handle them one by one?
            for (final var exception : exceptions) {
                exception.printStackTrace();
            }
            throw exceptions.get(0);
        }
    }

    /**
     * @see TablesV1API#createRow(long, JsonElement)
     */
    @NonNull
    private JsonElement serialize(@NonNull List<Column> columns, @NonNull TablesVersion version, @NonNull Data[] dataset) {
        final var properties = new JsonObject();

        for (final var data : dataset) {
            properties.add(String.valueOf(data.getRemoteColumnId()), serialize(getTypeForData(columns, data), version, data));
        }

        return properties;
    }

    /**
     * @return {@link JsonElement} representing the {@link Data#getValue()}
     */
    @NonNull
    private JsonElement serialize(@NonNull EDataType type, @NonNull TablesVersion version, @NonNull Data data) {
        final var value = data.getValue();
        return value == null
                ? JsonNull.INSTANCE
                : type.interceptRequest(version, data.getValue());
    }

    @NonNull
    private JsonElement interceptResponse(@NonNull EDataType type, @NonNull TablesVersion version, @Nullable JsonElement value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        }

        return type.interceptResponse(version, value);
    }

    private EDataType getTypeForData(@NonNull List<Column> columns, @NonNull Data data) {
        for (final var column : columns) {
            if (column.getId() == data.getColumnId()) {
                return EDataType.findByColumn(column);
            }
        }

        if (BuildConfig.DEBUG) {
            throw new IllegalStateException("Failed to find column for " + data + " (remoteColumnId: " + data.getRemoteColumnId() + ")");
        }

        return EDataType.UNKNOWN;
    }
}
