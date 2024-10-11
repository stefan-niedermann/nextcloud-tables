package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.FetchRowResponseV1Mapper;

public class RowSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = RowSyncAdapter.class.getSimpleName();
    private final ExecutorService rowFetchExecutor;
    private final FetchRowResponseV1Mapper fetchRowMapper;

    public RowSyncAdapter(@NonNull TablesDatabase db, @NonNull Context context) {
        this(db, context, Executors.newCachedThreadPool(), new FetchRowResponseV1Mapper());
    }

    private RowSyncAdapter(@NonNull TablesDatabase db,
                           @NonNull Context context,
                           @NonNull ExecutorService rowFetchExecutor,
                           @NonNull FetchRowResponseV1Mapper fetchRowMapper) {
        super(db, context);
        this.rowFetchExecutor = rowFetchExecutor;
        this.fetchRowMapper = fetchRowMapper;
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

        final var fullRowsToUpdate = db.getRowDao().getLocallyEditedRows(account.getId());
        Log.v(TAG, "------ Pushing " + rowsToDelete.size() + " local row changes for " + account.getAccountName());

        for (final var fullRow : fullRowsToUpdate) {
            Log.i(TAG, "------ → PUT/POST: " + fullRow.getRow().getRemoteId());

            if (fullRow.getRow().getRemoteId() == null) {
                final var response = apiV2.createRow(
                        ENodeTypeV2Dto.TABLE,
                        db.getTableDao().getRemoteId(fullRow.getRow().getTableId()),
                        fetchRowMapper.toJsonElement(version, fullRow.getFullData())).execute();

                if (response.isSuccessful()) {
                    fullRow.getRow().setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null || body.ocs == null || body.ocs.data == null) {
                        throw new NullPointerException("Pushing changes for row " + fullRow.getRow().getRemoteId() + " was successfully, but response body was empty");
                    }

                    fullRow.getRow().setRemoteId(body.ocs.data.remoteId());
                    db.getRowDao().update(fullRow.getRow());
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for row " + fullRow.getRow().getRemoteId());
                }
            } else {
                final var response = apiV1.updateRow(
                        fullRow.getRow().getRemoteId(),
                        fetchRowMapper.toJsonElement(version, fullRow.getFullData())).execute();
                Log.i(TAG, "------ → HTTP " + response.code());
                if (response.isSuccessful()) {
                    fullRow.getRow().setStatus(DBStatus.VOID);
                    final var body = response.body();
                    if (body == null) {
                        throw new NullPointerException("Pushing changes for row " + fullRow.getRow().getRemoteId() + " was successfully, but response body was empty");
                    }

                    fullRow.getRow().setRemoteId(body.remoteId());
                    db.getRowDao().update(fullRow.getRow());
                } else {
                    serverErrorHandler.handle(response, "Could not push local changes for row " + fullRow.getRow().getRemoteId());
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
                    final var columns = db.getColumnDao().getNotDeletedRemoteIdsAndColumns(table.getId());
                    final var columnRemoteAndLocalIds = db.getColumnDao().getColumnRemoteAndLocalIds(table.getId());
                    final var fetchedRows = new HashSet<FullRow>();
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
                                    final var fullRow = fetchRowMapper.toEntity(account.getId(), rowDto, columns, account.getTablesVersion());

                                    final var row = fullRow.getRow();
                                    row.setAccountId(table.getAccountId());
                                    row.setTableId(table.getId());
                                    row.setETag(response.headers().get(HEADER_ETAG));

                                    final var rowId = Optional.ofNullable(row.getRemoteId())
                                            .map(remoteId -> db.getRowDao().getRowId(table.getId(), remoteId));

                                    if (rowId.isEmpty()) {
                                        Log.i(TAG, "------ ← Adding table " + table.getTitle() + " to database");
                                        row.setId(db.getRowDao().insert(row));

                                    } else {
                                        Log.i(TAG, "------ ← Updating row " + row.getRemoteId() + " in database");
                                        row.setId(rowId.get());
                                        db.getRowDao().update(row);
                                    }

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

                                    for (final var fullData : fullRow.getFullData()) {
                                        final var data = fullData.getData();
                                        data.setRowId(row.getId());

                                        if (columnRemoteAndLocalIds.containsKey(data.getRemoteColumnId())) {
                                            final var existingData = db.getDataDao().getDataIdForCoordinates(data.getColumnId(), data.getRowId());
                                            if (existingData == null) {
                                                db.getDataDao().insert(data);
                                            } else {
                                                data.setId(existingData);
                                                db.getDataDao().update(data);
                                            }

                                            // Data deletion is handled by database constraints
                                        } else {
                                            Log.w(TAG, "------ Could not find remoteColumnId " + data.getRemoteColumnId() + ". Probably this column has been deleted but its data is still being responded by the server (See https://github.com/nextcloud/tables/issues/257)");

                                        }
                                    }
                                    fetchedRows.add(fullRow);
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

                    final var fetchedRowRemoteIds = fetchedRows.stream()
                            .map(FullRow::getRow)
                            .map(AbstractRemoteEntity::getRemoteId)
                            .collect(toUnmodifiableSet());

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
}
