package it.niedermann.nextcloud.tables.repository;

import android.content.Context;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EPermissionV2Dto;
import it.niedermann.nextcloud.tables.repository.exception.InsufficientPermissionException;
import it.niedermann.nextcloud.tables.repository.sync.AbstractSyncAdapter;
import it.niedermann.nextcloud.tables.repository.sync.ColumnSyncAdapter;
import it.niedermann.nextcloud.tables.repository.sync.RowSyncAdapter;
import it.niedermann.nextcloud.tables.repository.sync.TableSyncAdapter;
import it.niedermann.nextcloud.tables.repository.util.ColumnReorderUtil;

@WorkerThread
public class TablesRepository extends AbstractSyncAdapter {

    private static final String TAG = TablesRepository.class.getSimpleName();
    private final Context context;
    private final AbstractSyncAdapter tableSyncAdapter;
    private final AbstractSyncAdapter columnSyncAdapter;
    private final AbstractSyncAdapter rowSyncAdapter;
    private final ColumnReorderUtil columnReorderUtil;

    public TablesRepository(@NonNull Context context) {
        this(TablesDatabase.getInstance(context), context);
    }

    private TablesRepository(@NonNull TablesDatabase db,
                             @NonNull Context context) {
        this(db,
                context,
                new TableSyncAdapter(db, context),
                new ColumnSyncAdapter(db, context),
                new RowSyncAdapter(db, context),
                new ColumnReorderUtil());
    }

    private TablesRepository(@NonNull TablesDatabase db,
                             @NonNull Context context,
                             @NonNull AbstractSyncAdapter tableSyncAdapter,
                             @NonNull AbstractSyncAdapter columnSyncAdapter,
                             @NonNull AbstractSyncAdapter rowSyncAdapter,
                             @NonNull ColumnReorderUtil columnReorderUtil) {
        super(db, context);
        this.context = context;
        this.tableSyncAdapter = tableSyncAdapter;
        this.columnSyncAdapter = columnSyncAdapter;
        this.rowSyncAdapter = rowSyncAdapter;
        this.columnReorderUtil = columnReorderUtil;
    }

    public void synchronizeTables(@NonNull Account account) throws Exception {
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            final var api = apiProvider.getApi();
            final var apiV1 = apiV1Provider.getApi();

            pushLocalChanges(api, apiV1, account);
            pullRemoteChanges(api, apiV1, account);
        }
    }

    @Override
    public void pushLocalChanges(@NonNull TablesV2API api,
                                 @NonNull TablesV1API apiV1,
                                 @NonNull Account account) throws Exception {
        tableSyncAdapter.pushLocalChanges(api, apiV1, account);
        columnSyncAdapter.pushLocalChanges(api, apiV1, account);
        rowSyncAdapter.pushLocalChanges(api, apiV1, account);
    }

    @Override
    public void pullRemoteChanges(@NonNull TablesV2API api,
                                  @NonNull TablesV1API apiV1,
                                  @NonNull Account account) throws Exception {
        tableSyncAdapter.pullRemoteChanges(api, apiV1, account);
        columnSyncAdapter.pullRemoteChanges(api, apiV1, account);
        rowSyncAdapter.pullRemoteChanges(api, apiV1, account);
    }

    public LiveData<List<Table>> getNotDeletedTables$(@NonNull Account account, boolean isShared) {
        return db.getTableDao().getNotDeletedTables$(account.getId(), isShared);
    }

    @MainThread
    public LiveData<Table> getNotDeletedTable$(long id) {
        return Transformations.distinctUntilChanged(db.getTableDao().getNotDeletedTable$(id));
    }

    @MainThread
    public LiveData<List<Row>> getNotDeletedRows$(@NonNull Table table) {
        return Transformations.distinctUntilChanged(db.getRowDao().getNotDeletedRows$(table.getId()));
    }

    public void createTable(@NonNull Account account, @NonNull Table table) throws Exception {
        table.setStatus(DBStatus.LOCAL_EDITED);
        table.setAccountId(account.getId());
        db.getTableDao().insert(table);
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void updateTable(@NonNull Account account, @NonNull Table table) throws Exception {
        if (!table.hasManagePermission()) {
            throw new InsufficientPermissionException(EPermissionV2Dto.MANAGE);
        }
        table.setStatus(DBStatus.LOCAL_EDITED);
        db.getTableDao().update(table);
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void deleteTable(@NonNull Table table) throws Exception {
        if (!table.hasManagePermission()) {
            throw new InsufficientPermissionException(EPermissionV2Dto.MANAGE);
        }
        table.setStatus(DBStatus.LOCAL_DELETED);
        db.getTableDao().update(table);
        final var account = db.getAccountDao().getAccountById(table.getAccountId());
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void createColumn(@NonNull Account account, @NonNull Table table, @NonNull Column column) throws Exception {
        if (!table.hasManagePermission()) {
            throw new InsufficientPermissionException(EPermissionV2Dto.MANAGE);
        }
        column.setStatus(DBStatus.LOCAL_EDITED);
        column.setAccountId(account.getId());
        db.getColumnDao().insert(column);
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void reorderColumn(@NonNull Account account, long tableId, @NonNull List<Long> newColumnOrder) throws Exception {
        final var originalOrderWeights = db.getColumnDao().getNotDeletedOrderWeights(tableId);
        final var newOrderWeights = columnReorderUtil.reorderColumns(originalOrderWeights, newColumnOrder);
        final var newOrderWeightsDiff = columnReorderUtil.filterChanged(originalOrderWeights, newOrderWeights);
        for (final var entry : newOrderWeightsDiff.entrySet()) {
            db.getColumnDao().updateOrderWeight(entry.getKey(), entry.getValue());
        }
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void updateColumn(@NonNull Account account, @NonNull Table table, @NonNull Column column) throws Exception {
        if (!table.hasManagePermission()) {
            throw new InsufficientPermissionException(EPermissionV2Dto.MANAGE);
        }
        column.setStatus(DBStatus.LOCAL_EDITED);
        db.getColumnDao().update(column);
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void deleteColumn(@NonNull Table table, @NonNull Column column) throws Exception {
        if (!table.hasManagePermission()) {
            throw new InsufficientPermissionException(EPermissionV2Dto.MANAGE);
        }
        column.setStatus(DBStatus.LOCAL_DELETED);
        db.getColumnDao().update(column);
        final var account = db.getAccountDao().getAccountById(column.getAccountId());
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void createRow(@NonNull Account account, @NonNull Table table, @NonNull Row row, @NonNull Data[] dataset) throws Exception {
        if (!table.hasCreatePermission()) {
            throw new InsufficientPermissionException(EPermissionV2Dto.CREATE);
        }
        row.setStatus(DBStatus.LOCAL_EDITED);
        row.setAccountId(account.getId());
        final var insertedRowId = db.getRowDao().insert(row);
        for (final var data : dataset) {
            data.setRowId(insertedRowId);
            db.getDataDao().insert(data);
        }
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void updateRow(@NonNull Account account, @NonNull Table table, @NonNull Row row, @NonNull Data[] dataset) throws Exception {
        if (!table.hasUpdatePermission()) {
            throw new InsufficientPermissionException(EPermissionV2Dto.UPDATE);
        }
        row.setStatus(DBStatus.LOCAL_EDITED);
        row.setAccountId(account.getId());
        db.getRowDao().update(row);
        for (final var data : dataset) {
            data.setRowId(row.getId());
            if (data.getValue() == null) {
                db.getDataDao().delete(data);
            }
            final var exists = db.getDataDao().exists(data.getColumnId(), data.getRowId());
            if (exists) {
                db.getDataDao().update(data);
            } else {
                data.setId(db.getDataDao().insert(data));
            }
        }
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    public void deleteRow(@NonNull Table table, @NonNull Row row) throws Exception {
        if (!table.hasDeletePermission()) {
            throw new InsufficientPermissionException(EPermissionV2Dto.DELETE);
        }
        row.setStatus(DBStatus.LOCAL_DELETED);
        db.getRowDao().update(row);
        final var account = db.getAccountDao().getAccountById(row.getAccountId());
        try (
                final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
        ) {
            pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
        }
    }

    @MainThread
    public LiveData<List<Column>> getNotDeletedColumns$(@NonNull Table table) {
        return Transformations.distinctUntilChanged(db.getColumnDao().getNotDeletedColumns$(table.getId()));
    }

    public List<Column> getNotDeletedColumns(@NonNull Table table) {
        final var columns = db.getColumnDao().getNotDeletedColumns(table.getId());
        for (final var column : columns) {
            // TODO perf: move to one query?
            final var selectionOptions = db.getSelectionOptionDao().getNotDeletedSelectionOptions(column.getId());
            column.setSelectionOptions(selectionOptions);
        }
        return columns;
    }

    @MainThread
    public LiveData<List<Data>> getData(@NonNull Table table) {
        return Transformations.distinctUntilChanged(db.getDataDao().getData(table.getId()));
    }

    public Data[] getRawData(long rowId) {
        return db.getDataDao().getDataForRow(rowId);
    }

    @MainThread
    public LiveData<List<SelectionOption>> getUsedSelectionOptions(@NonNull Table table) {
        return Transformations.distinctUntilChanged(db.getSelectionOptionDao().getUsedSelectionOptionsById(table.getId()));
    }
}
