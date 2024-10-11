package it.niedermann.nextcloud.tables.repository;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.FullTable;
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

    @MainThread
    public TablesRepository(@NonNull Context context) {
        this(TablesDatabase.getInstance(context), context);
    }

    @MainThread
    private TablesRepository(@NonNull TablesDatabase db,
                             @NonNull Context context) {
        this(db,
                context,
                new TableSyncAdapter(db, context),
                new ColumnSyncAdapter(db, context),
                new RowSyncAdapter(db, context),
                new ColumnReorderUtil());
    }

    @MainThread
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

    @NonNull
    public CompletableFuture<Account> synchronizeTables(@NonNull Account account) {
        return supplyAsync(() -> {
            try (
                    final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                    final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
            ) {
                final var api = apiProvider.getApi();
                final var apiV1 = apiV1Provider.getApi();

                pushLocalChanges(api, apiV1, account);
                pullRemoteChanges(api, apiV1, account);

                return account;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, syncExecutor);
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
        return new ReactiveLiveData<>(db.getTableDao().getNotDeletedTables$(account.getId(), isShared))
                .distinctUntilChanged();
    }

    @MainThread
    public LiveData<Table> getNotDeletedTable$(long id) {
        return new ReactiveLiveData<>(db.getTableDao().getNotDeletedTable$(id))
                .distinctUntilChanged();
    }

    @MainThread
    public LiveData<List<FullRow>> getNotDeletedRows$(@NonNull Table table) {
        return new ReactiveLiveData<>(db.getRowDao().getNotDeletedRows$(table.getId()))
                .distinctUntilChanged();
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> createTable(@NonNull Account account,
                                               @NonNull Table table) {
        table.setStatus(DBStatus.LOCAL_EDITED);
        table.setAccountId(account.getId());
        return CompletableFuture.runAsync(() -> db.getTableDao().insert(table), dbSequentialExecutor)
                .thenRunAsync(() -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    public CompletableFuture<Void> updateTable(@NonNull Account account,
                                               @NonNull Table table) {
        return runAsync(() -> {
            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }
            table.setStatus(DBStatus.LOCAL_DELETED);
        }, syncExecutor)
                .thenRunAsync(() -> db.getTableDao().update(table), dbSequentialExecutor)
                .thenRunAsync(() -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    @AnyThread
    public CompletableFuture<Void> deleteTable(@NonNull Table table) {
        return runAsync(() -> {
            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }
            table.setStatus(DBStatus.LOCAL_DELETED);
        }, syncExecutor)
                .thenRunAsync(() ->
                        db.getTableDao().update(table), dbSequentialExecutor)
                .thenComposeAsync((v) -> CompletableFuture.completedFuture(
                        db.getAccountDao().getAccountById(table.getAccountId())), dbParallelExecutor)
                .thenAcceptAsync(account -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> createColumn(@NonNull Account account,
                                                @NonNull Table table,
                                                @NonNull Column column) {
        return runAsync(() -> {
            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }
            table.setStatus(DBStatus.LOCAL_DELETED);
            column.setAccountId(account.getId());
        }, syncExecutor)
                .thenComposeAsync(v -> CompletableFuture.completedFuture(db.getColumnDao().insert(column)), dbSequentialExecutor)
                .thenAcceptAsync(columnId -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> updateColumn(@NonNull Account account,
                                                @NonNull Table table,
                                                @NonNull Column column) {
        return runAsync(() -> {
            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }
            column.setStatus(DBStatus.LOCAL_EDITED);
        }, syncExecutor)
                .thenAcceptAsync(v -> db.getColumnDao().update(column), dbSequentialExecutor)
                .thenAcceptAsync(a -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> reorderColumn(@NonNull Account account,
                                                 long tableId,
                                                 @NonNull List<Long> newColumnOrder) {
        return supplyAsync(() -> db.getColumnDao().getNotDeletedOrderWeights(tableId), dbParallelExecutor)
                .thenAcceptAsync(originalOrderWeights -> {
                    final var newOrderWeights = columnReorderUtil.reorderColumns(originalOrderWeights, newColumnOrder);
                    final var newOrderWeightsDiff = columnReorderUtil.filterChanged(originalOrderWeights, newOrderWeights);
                    for (final var entry : newOrderWeightsDiff.entrySet()) {
                        db.getColumnDao().updateOrderWeight(entry.getKey(), entry.getValue());
                    }
                }, dbSequentialExecutor)
                .thenRunAsync(() -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteColumn(@NonNull Table table, @NonNull Column column) {
        return CompletableFuture.runAsync(() -> {
                    if (!table.hasManagePermission()) {
                        throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
                    }
                    column.setStatus(DBStatus.LOCAL_DELETED);
                }, syncExecutor)
                .thenRunAsync(() -> db.getColumnDao().update(column), dbSequentialExecutor)
                .thenComposeAsync(v -> CompletableFuture.completedFuture(db.getAccountDao().getAccountById(column.getAccountId())), dbParallelExecutor)
                .thenAcceptAsync(account -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> createRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<FullData> fullDataSet) {
        return CompletableFuture.runAsync(() -> {
                    if (!table.hasManagePermission()) {
                        throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.CREATE));
                    }
                    row.setStatus(DBStatus.LOCAL_EDITED);
                    row.setAccountId(account.getId());
                }, syncExecutor)
                .thenRunAsync(() -> {
                    final var insertedRowId = db.getRowDao().insert(row);
                    for (final var fullData : fullDataSet) {
                        fullData.getData().setRowId(insertedRowId);
                        db.getDataDao().insert(fullData.getData());
                    }
                }, dbSequentialExecutor)
                .thenRunAsync(() -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                });
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> updateRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<FullData> fullDataSet) {
        return CompletableFuture.runAsync(() -> {
                    if (!table.hasUpdatePermission()) {
                        throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.UPDATE));
                    }
                    row.setStatus(DBStatus.LOCAL_EDITED);
                    row.setAccountId(account.getId());
                }, syncExecutor)
                .thenRunAsync(() -> {
                    db.getRowDao().update(row);
                    for (final var data : fullDataSet) {
                        data.getData().setRowId(row.getId());

                        db.getDataDao().deleteRowIfEmpty(row.getId());

                        final var exists = db.getDataDao().exists(data.getData().getColumnId(), data.getData().getRowId());
                        if (exists) {
                            db.getDataDao().update(data.getData());
                        } else {
                            data.getData().setId(db.getDataDao().insert(data.getData()));
                        }
                    }
                }, dbSequentialExecutor)
                .thenRunAsync(() -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteRow(@NonNull Table table, @NonNull Row row) {
        return CompletableFuture.runAsync(() -> {
                    if (!table.hasDeletePermission()) {
                        throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.DELETE));
                    }
                    row.setStatus(DBStatus.LOCAL_DELETED);
                }, syncExecutor)
                .thenRunAsync(() -> db.getRowDao().update(row), dbSequentialExecutor)
                .thenComposeAsync(v -> CompletableFuture.completedFuture(db.getAccountDao().getAccountById(row.getAccountId())), dbParallelExecutor)
                .thenAcceptAsync(account -> {
                    try (
                            final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account);
                            final var apiV1Provider = ApiProvider.getTablesV1ApiProvider(context, account);
                    ) {
                        pushLocalChanges(apiProvider.getApi(), apiV1Provider.getApi(), account);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }, syncExecutor);
    }

    @MainThread
    public LiveData<List<FullColumn>> getNotDeletedFullColumns$(@NonNull Table table) {
        return new ReactiveLiveData<>(db.getColumnDao().getNotDeletedFullColumns$(table.getId()))
                .distinctUntilChanged();
    }

    @AnyThread
    @NonNull
    public CompletableFuture<List<FullColumn>> getNotDeletedColumns(@NonNull Table table) {
        return supplyAsync(() -> db.getColumnDao().getNotDeletedColumns(table.getId()), dbParallelExecutor);
    }

    @MainThread
    public LiveData<List<Data>> getData(@NonNull Table table) {
        return new ReactiveLiveData<>(db.getDataDao().getData(table.getId()))
                .distinctUntilChanged();
    }

    @AnyThread
    public CompletableFuture<Map<Long, FullData>> getRawColumnIdAndFullData(long rowId) {
        return supplyAsync(() -> db.getDataDao().getColumnIdAndFullData(rowId), dbParallelExecutor);
    }

    @MainThread
    public LiveData<FullTable> getFullTable$(long tableId) {
        return db.getTableDao().getFullTable$(tableId);
    }
}
