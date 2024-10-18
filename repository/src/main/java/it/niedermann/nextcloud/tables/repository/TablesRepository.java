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
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.FullTable;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EPermissionV2Dto;
import it.niedermann.nextcloud.tables.repository.exception.InsufficientPermissionException;
import it.niedermann.nextcloud.tables.repository.util.ColumnReorderUtil;

@WorkerThread
public class TablesRepository extends AbstractRepository {

    private static final String TAG = TablesRepository.class.getSimpleName();
    private final ColumnReorderUtil columnReorderUtil;

    @MainThread
    public TablesRepository(@NonNull Context context) {
        super(context);
        this.columnReorderUtil = new ColumnReorderUtil();
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
        return supplyAsync(() -> {
            table.setStatus(DBStatus.LOCAL_EDITED);
            table.setAccountId(account.getId());
            return table;
        }, workExecutor)
                .thenAcceptAsync(db.getTableDao()::insert, db.getSequentialExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
    }

    public CompletableFuture<Void> updateTable(@NonNull Account account,
                                               @NonNull Table table) {
        return supplyAsync(() -> {
            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }
            table.setStatus(DBStatus.LOCAL_DELETED);
            return table;
        }, workExecutor)
                .thenRunAsync(db.getTableDao()::update, db.getSequentialExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
    }

    @AnyThread
    public CompletableFuture<Void> deleteTable(@NonNull Table table) {
        return supplyAsync(() -> {
            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }
            table.setStatus(DBStatus.LOCAL_DELETED);
            return table;
        }, workExecutor)
                .thenRunAsync(db.getTableDao()::update, db.getSequentialExecutor())
                .thenApplyAsync(v -> db.getAccountDao().getAccountById(table.getAccountId()), db.getParallelExecutor())
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
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
        }, workExecutor)
                .thenApplyAsync(v -> db.getColumnDao().insert(column), db.getSequentialExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
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
        }, workExecutor)
                .thenRunAsync(() -> db.getColumnDao().update(column), db.getSequentialExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> reorderColumn(@NonNull Account account,
                                                 long tableId,
                                                 @NonNull List<Long> newColumnOrder) {
        return supplyAsync(() -> db.getColumnDao().getNotDeletedOrderWeights(tableId), db.getParallelExecutor())
                .thenApplyAsync(originalOrderWeights -> {
                    final var newOrderWeights = columnReorderUtil.reorderColumns(originalOrderWeights, newColumnOrder);
                    final var newOrderWeightsDiff = columnReorderUtil.filterChanged(originalOrderWeights, newOrderWeights);
                    for (final var entry : newOrderWeightsDiff.entrySet()) {
                        db.getColumnDao().updateOrderWeight(entry.getKey(), entry.getValue());
                    }
                    return account;
                }, db.getSequentialExecutor())
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteColumn(@NonNull Table table, @NonNull Column column) {
        return runAsync(() -> {
            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }
            column.setStatus(DBStatus.LOCAL_DELETED);
        }, workExecutor)
                .thenRunAsync(() -> db.getColumnDao().update(column), db.getSequentialExecutor())
                .thenApplyAsync(v -> db.getAccountDao().getAccountById(column.getAccountId()), db.getParallelExecutor())
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> createRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<FullData> fullDataSet) {
        return runAsync(() -> {
            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.CREATE));
            }
            row.setStatus(DBStatus.LOCAL_EDITED);
            row.setAccountId(account.getId());
        }, workExecutor)
                .thenRunAsync(() -> {
                    final var insertedRowId = db.getRowDao().insert(row);
                    for (final var fullData : fullDataSet) {
                        fullData.getData().setRowId(insertedRowId);
                        db.getDataDao().insert(fullData.getData());
                    }
                }, db.getSequentialExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> updateRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<FullData> fullDataSet) {
        return runAsync(() -> {
            if (!table.hasUpdatePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.UPDATE));
            }
            row.setStatus(DBStatus.LOCAL_EDITED);
            row.setAccountId(account.getId());
        }, workExecutor)
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
                }, db.getSequentialExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteRow(@NonNull Table table, @NonNull Row row) {
        return runAsync(() -> {
            if (!table.hasDeletePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.DELETE));
            }
            row.setStatus(DBStatus.LOCAL_DELETED);
        }, workExecutor)
                .thenRunAsync(() -> db.getRowDao().update(row), db.getSequentialExecutor())
                .thenApplyAsync(v -> db.getAccountDao().getAccountById(row.getAccountId()), db.getParallelExecutor())
                .thenAcceptAsync(syncAdapter::synchronize, workExecutor);
    }

    @MainThread
    public LiveData<List<FullColumn>> getNotDeletedFullColumns$(@NonNull Table table) {
        return new ReactiveLiveData<>(db.getColumnDao().getNotDeletedFullColumns$(table.getId()))
                .distinctUntilChanged();
    }

    @AnyThread
    @NonNull
    public CompletableFuture<List<FullColumn>> getNotDeletedColumns(@NonNull Table table) {
        return supplyAsync(() -> db.getColumnDao().getNotDeletedColumns(table.getId()), db.getParallelExecutor());
    }

    @MainThread
    public LiveData<List<Data>> getData(@NonNull Table table) {
        return new ReactiveLiveData<>(db.getDataDao().getData(table.getId()))
                .distinctUntilChanged();
    }

    @AnyThread
    public CompletableFuture<Map<Long, FullData>> getRawColumnIdAndFullData(long rowId) {
        return supplyAsync(() -> db.getDataDao().getColumnIdAndFullData(rowId), db.getParallelExecutor());
    }

    @MainThread
    public LiveData<FullTable> getFullTable$(long tableId) {
        return db.getTableDao().getFullTable$(tableId);
    }
}
