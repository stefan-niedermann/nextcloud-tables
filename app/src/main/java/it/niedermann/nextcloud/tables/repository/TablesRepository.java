package it.niedermann.nextcloud.tables.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.util.List;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;

@WorkerThread
public class TablesRepository {

    private static final String TAG = TablesRepository.class.getSimpleName();
    private static final String HEADER_ETAG = "ETag";
    private final Context context;
    private final TablesDatabase db;

    public TablesRepository(@NonNull Context context) {
        this.context = context;
        this.db = TablesDatabase.getInstance(context);
    }

    public void synchronizeTables(@NonNull Account account) throws NextcloudFilesAppAccountNotFoundException, IOException, NextcloudHttpRequestFailedException {
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            pushLocalTables(apiProvider.getApi(), account);
            pullRemoteTables(apiProvider.getApi(), account);
        }
    }

    private void pushLocalTables(@NonNull TablesAPI tablesApi, @NonNull Account account) throws IOException, NextcloudHttpRequestFailedException {
        Log.v(TAG, "Pushing local changes for " + account.getAccountName());
        final var deletedTables = db.getTableDao().getTables(account.getId(), DBStatus.LOCAL_DELETED);
        for (final var table : deletedTables) {
            Log.i(TAG, "→ DELETE: " + table.getTitle());
            final var response = tablesApi.deleteTable(table.getRemoteId()).execute();
            Log.i(TAG, "-→ HTTP " + response.code());
            if (response.isSuccessful()) {
                db.getTableDao().delete(table);
            } else {
                throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException("Could not delete table " + table.getTitle()));
            }
        }

        final var changedTables = db.getTableDao().getTables(account.getId(), DBStatus.LOCAL_EDITED);
        for (final var table : changedTables) {
            Log.i(TAG, "→ PUT: " + table.getTitle());
            final var response = tablesApi.updateTable(table.getRemoteId(), table.getTitle(), table.getEmoji()).execute();
            Log.i(TAG, "-→ HTTP " + response.code());
            if (response.isSuccessful()) {
                db.getTableDao().updateStatus(table.getId(), DBStatus.VOID);
            } else {
                throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException("Could not push local changes for table " + table.getTitle()));
            }
        }
    }

    private void pullRemoteTables(@NonNull TablesAPI tablesApi, @NonNull Account account) throws IOException, NextcloudHttpRequestFailedException {
        Log.v(TAG, "Pulling remote changes for " + account.getAccountName());
        final var request = tablesApi.getTables();
        final var response = request.execute();
        switch (response.code()) {
            case 200: {
                final var tables = response.body();
                if (tables == null) {
                    throw new RuntimeException("Response body is null");
                }

                for (final var table : tables) {
                    Log.i(TAG, "→ Adding " + table.getTitle() + " to database");
                    table.setStatus(DBStatus.VOID);
                    table.setAccountId(account.getId());
                    table.setETag(response.headers().get(HEADER_ETAG));
                    final var createdTable = db.getTableDao().getTable(db.getTableDao().insert(table));
                    pullRemoteColumns(tablesApi, createdTable);
                    pullRemoteRows(tablesApi, createdTable);
                }
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

    public void pushLocalColumns(@NonNull ApiProvider<TablesAPI> apiProvider, @NonNull Account account) throws IOException {
        // TODO API not available yet
    }

    public void pullRemoteColumns(@NonNull TablesAPI tablesApi, @NonNull Table table) throws IOException, NextcloudHttpRequestFailedException {
        final var request = tablesApi.getColumns(table.getRemoteId());
        final var response = request.execute();
        switch (response.code()) {
            case 200: {
                final var body = response.body();
                if (body == null) {
                    throw new RuntimeException("Response body is null");
                }

                for (final var column : body) {
                    column.setAccountId(table.getAccountId());
                    column.setTableId(table.getId());
                    table.setETag(response.headers().get(HEADER_ETAG));
                    db.getColumnDao().insert(column);
                }
                break;
            }

            case 304: {
                Log.v(TAG, "Pull remote columns: HTTP " + response.code() + " Not Modified");
                break;
            }

            case 405: {
                // TODO https://github.com/nextcloud/tables/pull/170/files#r1151644073
                Log.w(TAG, "Pull remote columns: HTTP " + response.code());
                break;
            }

            default: {
                throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException());
            }
        }
    }

    public void pushLocalRows(@NonNull ApiProvider<TablesAPI> apiProvider, @NonNull Account account) throws IOException {
        // TODO API not available yet
    }

    public void pullRemoteRows(@NonNull TablesAPI tablesApi, @NonNull Table table) throws IOException, NextcloudHttpRequestFailedException {
        final var request = tablesApi.getRows(table.getRemoteId());
        final var response = request.execute();
        switch (response.code()) {
            case 200: {
                final var body = response.body();
                if (body == null) {
                    throw new RuntimeException("Response body is null");
                }

                for (final var row : body) {
                    row.setAccountId(table.getAccountId());
                    row.setTableId(table.getId());
                    row.setETag(response.headers().get(HEADER_ETAG));
                    final var insertedRow = db.getRowDao().insert(row);
                    for (final var data : row.getData()) {
                        data.setAccountId(table.getAccountId());
                        data.setRowId(insertedRow);
                        data.setColumnId(db.getColumnDao().getColumnId(table.getAccountId(), data.getRemoteColumnId()));
                        db.getDataDao().insert(data);
                    }
                }
                break;
            }

            case 304: {
                Log.v(TAG, "Pull remote rows: HTTP " + response.code() + " Not Modified");
                break;
            }

            case 405: {
                // TODO https://github.com/nextcloud/tables/pull/170/files#r1151644073
                Log.w(TAG, "Pull remote rows: HTTP " + response.code());
                break;
            }

            default: {
                throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException());
            }
        }
    }

    public LiveData<List<Table>> getNotDeletedTables$(@NonNull Account account, boolean isShared) {
        return db.getTableDao().getNotDeletedTables$(account.getId(), isShared);
    }

    public LiveData<Table> getTable(long id) {
        return db.getTableDao().getNotDeletedTable$(id);
    }

    public LiveData<List<Row>> getRows(@NonNull Table table) {
        return db.getRowDao().getNotDeletedRows$(table.getId());
    }

    public void createTable(@NonNull Account account, @NonNull Table table) throws NextcloudHttpRequestFailedException, IOException, NextcloudFilesAppAccountNotFoundException {
        table.setStatus(DBStatus.LOCAL_EDITED);
        table.setAccountId(account.getId());
        db.getTableDao().insert(table);
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            pushLocalTables(apiProvider.getApi(), account);
        }
    }

    public void updateTable(@NonNull Table table) throws NextcloudHttpRequestFailedException, IOException, NextcloudFilesAppAccountNotFoundException {
        table.setStatus(DBStatus.LOCAL_EDITED);
        db.getTableDao().update(table);
        final var account = db.getAccountDao().getAccountById(table.getAccountId());
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            pushLocalTables(apiProvider.getApi(), account);
        }
    }

    public void deleteTable(@NonNull Table table) throws NextcloudFilesAppAccountNotFoundException, NextcloudHttpRequestFailedException, IOException {
        table.setStatus(DBStatus.LOCAL_DELETED);
        db.getTableDao().update(table);
        final var account = db.getAccountDao().getAccountById(table.getAccountId());
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            pushLocalTables(apiProvider.getApi(), account);
        }
    }

    public LiveData<List<Column>> getColumns(@NonNull Table table) {
        return db.getColumnDao().getNotDeletedColumns$(table.getId());
    }

    public LiveData<List<Data>> getData(@NonNull Table table, List<Long> remoteColumnIds) {
        return db.getDataDao().getData(table.getId(), remoteColumnIds);
    }
}
