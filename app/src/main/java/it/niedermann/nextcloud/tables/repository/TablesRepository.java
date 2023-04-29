package it.niedermann.nextcloud.tables.repository;

import android.content.Context;

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
import it.niedermann.nextcloud.tables.repository.sync.AbstractSyncAdapter;
import it.niedermann.nextcloud.tables.repository.sync.ColumnSyncAdapter;
import it.niedermann.nextcloud.tables.repository.sync.RowSyncAdapter;
import it.niedermann.nextcloud.tables.repository.sync.TableSyncAdapter;

@WorkerThread
public class TablesRepository {

    private static final String TAG = TablesRepository.class.getSimpleName();
    private final Context context;
    private final TablesDatabase db;
    private final AbstractSyncAdapter tableSyncAdapter;
    private final AbstractSyncAdapter columnSyncAdapter;
    private final AbstractSyncAdapter rowSyncAdapter;

    public TablesRepository(@NonNull Context context) {
        this(context, TablesDatabase.getInstance(context));
    }

    private TablesRepository(@NonNull Context context,
                             @NonNull TablesDatabase db) {
        this(context, db, new TableSyncAdapter(db), new ColumnSyncAdapter(db), new RowSyncAdapter(db));
    }

    private TablesRepository(@NonNull Context context,
                             @NonNull TablesDatabase db,
                             @NonNull AbstractSyncAdapter tableSyncAdapter,
                             @NonNull AbstractSyncAdapter columnSyncAdapter,
                             @NonNull AbstractSyncAdapter rowSyncAdapter) {
        this.context = context;
        this.db = db;
        this.tableSyncAdapter = tableSyncAdapter;
        this.columnSyncAdapter = columnSyncAdapter;
        this.rowSyncAdapter = rowSyncAdapter;
    }

    public void synchronizeTables(@NonNull Account account) throws NextcloudFilesAppAccountNotFoundException, IOException, NextcloudHttpRequestFailedException {
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            final var api = apiProvider.getApi();

            tableSyncAdapter.pushLocalChanges(api, account);
            columnSyncAdapter.pushLocalChanges(api, account);
            rowSyncAdapter.pushLocalChanges(api, account);

            tableSyncAdapter.pullRemoteChanges(api, account);
            columnSyncAdapter.pullRemoteChanges(api, account);
            rowSyncAdapter.pullRemoteChanges(api, account);
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
            tableSyncAdapter.pushLocalChanges(apiProvider.getApi(), account);
        }
    }

    public void updateTable(@NonNull Table table) throws NextcloudHttpRequestFailedException, IOException, NextcloudFilesAppAccountNotFoundException {
        table.setStatus(DBStatus.LOCAL_EDITED);
        db.getTableDao().update(table);
        final var account = db.getAccountDao().getAccountById(table.getAccountId());
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            tableSyncAdapter.pushLocalChanges(apiProvider.getApi(), account);
        }
    }

    public void deleteTable(@NonNull Table table) throws NextcloudFilesAppAccountNotFoundException, NextcloudHttpRequestFailedException, IOException {
        table.setStatus(DBStatus.LOCAL_DELETED);
        db.getTableDao().update(table);
        final var account = db.getAccountDao().getAccountById(table.getAccountId());
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            tableSyncAdapter.pushLocalChanges(apiProvider.getApi(), account);
        }
    }

    public void deleteColumn(@NonNull Column column) throws NextcloudFilesAppAccountNotFoundException, NextcloudHttpRequestFailedException, IOException {
        column.setStatus(DBStatus.LOCAL_DELETED);
        db.getColumnDao().update(column);
        final var account = db.getAccountDao().getAccountById(column.getAccountId());
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            columnSyncAdapter.pushLocalChanges(apiProvider.getApi(), account);
        }
    }

    public void createRow(@NonNull Account account, @NonNull Row row, @NonNull Data[] data) throws NextcloudFilesAppAccountNotFoundException, NextcloudHttpRequestFailedException, IOException {
        row.setStatus(DBStatus.LOCAL_EDITED);
        row.setAccountId(account.getId());
        final var insertedRowId = db.getRowDao().insert(row);
        for (final var d : data) {
            d.setRowId(insertedRowId);
            db.getDataDao().insert(d);
        }
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            rowSyncAdapter.pushLocalChanges(apiProvider.getApi(), account);
        }
    }

    public void updateRow(@NonNull Account account, @NonNull Row row, @NonNull Data[] data) throws NextcloudFilesAppAccountNotFoundException, NextcloudHttpRequestFailedException, IOException {
        row.setStatus(DBStatus.LOCAL_EDITED);
        row.setAccountId(account.getId());
        db.getRowDao().update(row);
        for (final var d : data) {
            d.setRowId(row.getId());
            db.getDataDao().update(d);
        }
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            rowSyncAdapter.pushLocalChanges(apiProvider.getApi(), account);
        }
    }

    public void deleteRow(@NonNull Row row) throws NextcloudFilesAppAccountNotFoundException, NextcloudHttpRequestFailedException, IOException {
        row.setStatus(DBStatus.LOCAL_DELETED);
        db.getRowDao().update(row);
        final var account = db.getAccountDao().getAccountById(row.getAccountId());
        try (final var apiProvider = ApiProvider.getTablesApiProvider(context, account)) {
            rowSyncAdapter.pushLocalChanges(apiProvider.getApi(), account);
        }
    }

    public LiveData<List<Column>> getNotDeletedColumns$(@NonNull Table table) {
        return db.getColumnDao().getNotDeletedColumns$(table.getId());
    }

    public List<Column> getNotDeletedColumns(@NonNull Table table) {
        return db.getColumnDao().getNotDeletedColumns(table.getId());
    }

    public LiveData<List<Data>> getData(@NonNull Table table) {
        return db.getDataDao().getData(table.getId());
    }

    public Data[] getRawData(long rowId) {
        return db.getDataDao().getDataForRow(rowId);
    }
}
