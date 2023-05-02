package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.util.Log;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.util.HashSet;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;


public class TableSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = TableSyncAdapter.class.getSimpleName();

    public TableSyncAdapter(@NonNull TablesDatabase db) {
        super(db);
    }

    @Override
    public void pushLocalChanges(@NonNull TablesAPI api, @NonNull Account account) throws IOException, NextcloudHttpRequestFailedException {
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
                    throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException("Could not delete table " + table.getTitle()));
                }
            }
        }

        final var changedTables = db.getTableDao().getTables(account.getId(), DBStatus.LOCAL_EDITED);
        for (final var table : changedTables) {
            Log.i(TAG, "→ PUT/POST: " + table.getTitle());
            final var response = table.getRemoteId() == null
                    ? api.createTable(table.getTitle(), table.getEmoji()).execute()
                    : api.updateTable(table.getRemoteId(), table.getTitle(), table.getEmoji()).execute();
            Log.i(TAG, "-→ HTTP " + response.code());
            if (response.isSuccessful()) {
                table.setStatus(DBStatus.VOID);
                table.setRemoteId(response.body().getRemoteId());
                db.getTableDao().update(table);
            } else {
                throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException("Could not push local changes for table " + table.getTitle()));
            }
        }
    }

    @Override
    public void pullRemoteChanges(@NonNull TablesAPI api, @NonNull Account account) throws IOException, NextcloudHttpRequestFailedException {
        final var fetchedTables = new HashSet<Table>();
        int offset = 0;

        fetchTablesLoop:
        while (true) {
            Log.v(TAG, "Pulling remote changes for " + account.getAccountName() + " (offset: " + offset + ")");
            final var request = api.getTables(TablesAPI.API_LIMIT_TABLES, offset);
            final var response = request.execute();
            switch (response.code()) {
                case 200: {
                    final var tables = response.body();
                    if (tables == null) {
                        throw new RuntimeException("Response body is null");
                    }

                    for (final var table : tables) {
                        table.setStatus(DBStatus.VOID);
                        table.setAccountId(account.getId());
                        table.setETag(response.headers().get(HEADER_ETAG));
                    }

                    fetchedTables.addAll(tables);

                    if (tables.size() != TablesAPI.API_LIMIT_TABLES) {
                        break fetchTablesLoop;
                    }

                    offset += tables.size();

                    break;
                }

                case 304: {
                    Log.v(TAG, "→ HTTP " + response.code() + " Not Modified");
                    break;
                }

                default: {
                    throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException());
                }
            }
        }

        final var tableRemoteIds = fetchedTables.stream().map(AbstractRemoteEntity::getRemoteId).collect(toUnmodifiableSet());
        final var tableIds = db.getTableDao().getTableRemoteAndLocalIds(account.getId(), tableRemoteIds);
        for (final var table : fetchedTables) {
            final var tableId = tableIds.get(table.getRemoteId());
            if (tableId == null) {
                Log.i(TAG, "→ Adding " + table.getTitle() + " to database");
                table.setId(db.getTableDao().insert(table));
            } else {
                table.setId(tableId);
                Log.i(TAG, "→ Updating " + table.getTitle() + " in database");
                db.getTableDao().update(table);
            }
        }

        Log.i(TAG, "→ Delete all tables except remoteId " + tableRemoteIds);
        db.getTableDao().deleteExcept(account.getId(), tableRemoteIds);
    }
}
