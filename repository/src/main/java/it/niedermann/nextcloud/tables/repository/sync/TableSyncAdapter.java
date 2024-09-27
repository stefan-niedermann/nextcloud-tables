package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashSet;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.TableV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.TableV2Mapper;


public class TableSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = TableSyncAdapter.class.getSimpleName();
    private final Mapper<TableV2Dto, Table> tableMapper;

    public TableSyncAdapter(@NonNull TablesDatabase db, @NonNull Context context) {
        super(db, context);
        this.tableMapper = new TableV2Mapper();
    }

    @Override
    public void pushLocalChanges(@NonNull TablesV2API api,
                                 @NonNull TablesV1API apiV1,
                                 @NonNull Account account) throws Exception {
        Log.v(TAG, "Pushing local changes for " + account.getAccountName());
        final var deletedTables = db.getTableDao().getTables(account.getId(), DBStatus.LOCAL_DELETED);
        for (final var table : deletedTables) {
            Log.i(TAG, "→ DELETE: " + table.getTitle());
            final var remoteId = table.getRemoteId();
            if (remoteId == null) {
                db.getTableDao().delete(table);
            } else {
                final var response = api.deleteTable(table.getRemoteId()).execute();
                Log.i(TAG, "-→ HTTP " + response.code());
                if (response.isSuccessful()) {
                    db.getTableDao().delete(table);
                } else {
                    serverErrorHandler.handle(response, "Could not delete table " + table.getTitle());
                }
            }
        }

        final var changedTables = db.getTableDao().getTables(account.getId(), DBStatus.LOCAL_EDITED);
        for (final var table : changedTables) {
            Log.i(TAG, "→ PUT/POST: " + table.getTitle());
            final var response = table.getRemoteId() == null
                    ? api.createTable(table.getTitle(),
                    table.getDescription(),
                    table.getEmoji(),
                    TablesV2API.DEFAULT_TABLES_TEMPLATE).execute()
                    : api.updateTable(table.getRemoteId(),
                    table.getTitle(),
                    table.getDescription(),
                    table.getEmoji()).execute();
            Log.i(TAG, "-→ HTTP " + response.code());
            if (response.isSuccessful()) {
                table.setStatus(DBStatus.VOID);
                final var body = response.body();
                if (body == null || body.ocs == null || body.ocs.data == null) {
                    throw new NullPointerException("Pushing changes for table " + table.getTitle() + " was successfull, but response body was empty");
                }

                table.setRemoteId(body.ocs.data.remoteId());
                db.getTableDao().update(table);
            } else {
                serverErrorHandler.handle(response, "Could not push local changes for table " + table.getTitle());
            }
        }
    }

    @Override
    public void pullRemoteChanges(@NonNull TablesV2API api,
                                  @NonNull TablesV1API apiV1,
                                  @NonNull Account account) throws Exception {
        final var fetchedTables = new HashSet<Table>();
        int offset = 0;

//        fetchTablesLoop:
//        while (true) {
        Log.v(TAG, "Pulling remote changes for " + account.getAccountName() + " (offset: " + offset + ")");
        final var request = api.getTables(/*TablesAPI.DEFAULT_API_LIMIT_TABLES, offset*/);
        final var response = request.execute();
        //noinspection SwitchStatementWithTooFewBranches
        switch (response.code()) {
            case 200: {
                final var responseBody = response.body();
                if (responseBody == null || responseBody.ocs == null || responseBody.ocs.data == null) {
                    throw new RuntimeException("Response body is null");
                }

                final var tableDtos = responseBody.ocs.data;

                for (final var tableDto : tableDtos) {
                    final var table = tableMapper.toEntity(tableDto);
                    table.setStatus(DBStatus.VOID);
                    table.setAccountId(account.getId());
                    table.setETag(response.headers().get(HEADER_ETAG));
                    fetchedTables.add(table);
                }

//                    if (tables.size() != TablesAPI.DEFAULT_API_LIMIT_TABLES) {
//                        break fetchTablesLoop;
//                    }

                offset += tableDtos.size();

                break;
            }

            default: {
                serverErrorHandler.handle(response);
            }
//            }
        }

        final var tableRemoteIds = fetchedTables.stream().map(AbstractRemoteEntity::getRemoteId).collect(toUnmodifiableSet());
        final var tableIds = db.getTableDao().getTableRemoteAndLocalIds(account.getId(), tableRemoteIds);
        for (final var table : fetchedTables) {
            final var tableId = tableIds.get(table.getRemoteId());
            if (tableId == null) {
                Log.i(TAG, "← Adding table " + table.getTitle() + " to database");
                table.setId(db.getTableDao().insert(table));
            } else {
                table.setId(tableId);
                Log.i(TAG, "← Updating " + table.getTitle() + " in database");
                db.getTableDao().update(table);
                if (!table.hasReadPermission()) {
                    db.getRowDao().deleteAllFromTable(table.getId());
                }
            }
        }

        Log.i(TAG, "← Delete all tables except remoteId " + tableRemoteIds);
        db.getTableDao().deleteExcept(account.getId(), tableRemoteIds);
    }
}
