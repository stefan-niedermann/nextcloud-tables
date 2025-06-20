package it.niedermann.nextcloud.tables.repository;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Pair;
import android.util.Range;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.DataSelectionOptionCrossRef;
import it.niedermann.nextcloud.tables.database.entity.DefaultValueSelectionOptionCrossRef;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.FullTable;
import it.niedermann.nextcloud.tables.database.model.LinkValueWithProviderId;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EPermissionV2Dto;
import it.niedermann.nextcloud.tables.repository.exception.InsufficientPermissionException;
import it.niedermann.nextcloud.tables.repository.util.ColumnReorderUtil;

@WorkerThread
public class TablesRepository extends AbstractRepository {

    private final Logger logger = Logger.getLogger(TablesRepository.class.getSimpleName());
    private final ColumnReorderUtil columnReorderUtil;

    @MainThread
    public TablesRepository(@NonNull Context context) {
        super(context);
        this.columnReorderUtil = new ColumnReorderUtil();
    }

    @MainThread
    public LiveData<List<Table>> getNotDeletedTables$(@NonNull Account account, boolean favorite, boolean archived) {
        return new ReactiveLiveData<>(db.getTableDao().getNotDeletedTables$(account.getId(), favorite, archived))
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

    @MainThread
    public LiveData<List<FullColumn>> getNotDeletedFullColumns$(@NonNull Table table) {
        return new ReactiveLiveData<>(db.getColumnDao().getNotDeletedFullColumns$(table.getId()))
                .distinctUntilChanged();
    }

    @MainThread
    public LiveData<List<Data>> getData(@NonNull Table table) {
        return new ReactiveLiveData<>(db.getDataDao().getData(table.getId()))
                .distinctUntilChanged();
    }

    /// @implNote Performs sorting of [Column]s and [Row]s, but not of the data itself on code level
    @MainThread
    public LiveData<FullTable> getFullTable$(long tableId, @NonNull Range<Long> rowPositions) {
        return new ReactiveLiveData<>(db.getTableDao().getFullTable$(tableId, rowPositions.getLower(), rowPositions.getUpper()))
                .distinctUntilChanged()
                .map(FullTable::new)
                .tap(fullTable -> {
                    logger.info(String.format(Locale.getDefault(), "PERF :: " + "getFullTable$(%d1, %d2, %d3)", tableId, rowPositions.getLower(), rowPositions.getUpper()));
                    logger.info("--------------------------------------------------------------");
                    // We must sort our data here because Rooms @Relation does not allow ordering within FullTable
                    // However, we only have to sort the columns and the rows, not the data itself because accessing the data happens via an index based access to the dataGrid
                    Collections.sort(fullTable.getColumns());
                    Collections.sort(fullTable.getRows());
                });
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
                .thenAcceptAsync(db.getTableDao()::insert, db.getUserInteractionWriteExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @AnyThread
    public CompletableFuture<Void> updateTable(@NonNull Account account,
                                               @NonNull Table table) {
        return supplyAsync(() -> {

            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }

            table.setStatus(DBStatus.LOCAL_EDITED);

            return table;

        }, workExecutor)
                .thenAcceptAsync(db.getTableDao()::update, db.getUserInteractionWriteExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @AnyThread
    public CompletableFuture<Void> deleteTable(@NonNull Table table) {
        return runAsync(() -> {

            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }

            table.setStatus(DBStatus.LOCAL_DELETED);

        }, workExecutor)
                .thenRunAsync(() -> db.runInTransaction(() -> {
                    db.getTableDao().update(table);
                    db.getAccountDao().guessCurrentTable(table.getAccountId());
                }), db.getUserInteractionWriteExecutor())
                .thenApplyAsync(v -> db.getAccountDao().getAccountById(table.getAccountId()), db.getUserInteractionReadExecutor())
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> createColumn(@NonNull Account account,
                                                @NonNull Table table,
                                                @NonNull FullColumn fullColumn) {
        final var column = fullColumn.getColumn();
        return supplyAsync(() -> {

            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }

            column.setStatus(DBStatus.LOCAL_EDITED);
            column.setAccountId(account.getId());

            return column;

        }, workExecutor)
                .thenComposeAsync(v -> switch (column.getDataType()) {

                    case SELECTION, SELECTION_MULTI -> runAsync(() -> db.runInTransaction(() -> {
                        final long insertedColumnId = db.getColumnDao().insert(column);
                        column.setId(insertedColumnId);

                        fullColumn.getSelectionOptions()
                                .stream()
                                .peek(item -> item.setColumnId(column.getId()))
                                .forEach(item -> item.setId(db.getSelectionOptionDao().insert(item)));

                        fullColumn.getDefaultSelectionOptions()
                                .stream()
                                .map(DefaultValueSelectionOptionCrossRef::from)
                                .forEach(db.getDefaultValueSelectionOptionCrossRefDao()::insert);
                    }), db.getUserInteractionWriteExecutor());

                    default -> completedFuture(column)
                            .thenApplyAsync(db.getColumnDao()::insert, db.getUserInteractionWriteExecutor())
                            .thenAcceptAsync(column::setId, workExecutor);

                }, workExecutor)
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> updateColumn(@NonNull Account account,
                                                @NonNull Table table,
                                                @NonNull FullColumn fullColumn) {
        final var column = fullColumn.getColumn();
        return supplyAsync(() -> {

            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }

            column.setStatus(DBStatus.LOCAL_EDITED);

            return column;

        }, workExecutor)
                .thenComposeAsync(v -> switch (column.getDataType()) {

                    case SELECTION, SELECTION_MULTI -> analyzeSelectionOptions(fullColumn)
                            .thenAcceptAsync(crudItems -> db.runInTransaction(() -> {
                                db.getColumnDao().update(column);

                                crudItems.toDelete()
                                        .forEach(db.getSelectionOptionDao()::delete);

                                crudItems.toUpdate()
                                        .stream()
                                        .peek(selectionOption -> selectionOption.setColumnId(column.getId()))
                                        .forEach(db.getSelectionOptionDao()::update);

                                crudItems.toInsert()
                                        .stream()
                                        .peek(selectionOption -> selectionOption.setColumnId(column.getId()))
                                        .forEach(selectionOption -> {
                                            final long selectionOptionId = db.getSelectionOptionDao().insert(selectionOption);
                                            selectionOption.setId(selectionOptionId);
                                        });

                                final var targetDefaultSelectionOptionIds = fullColumn.getDefaultSelectionOptions()
                                        .stream()
                                        .map(SelectionOption::getId)
                                        .filter(Objects::nonNull)
                                        .collect(toUnmodifiableSet());

                                db.getDefaultValueSelectionOptionCrossRefDao().deleteExcept(column.getId(), targetDefaultSelectionOptionIds);
                                fullColumn.getDefaultSelectionOptions()
                                        .stream()
                                        .map(DefaultValueSelectionOptionCrossRef::from)
                                        .forEach(db.getDefaultValueSelectionOptionCrossRefDao()::upsert);
                            }), db.getUserInteractionWriteExecutor());

                    default ->
                            runAsync(() -> db.getColumnDao().update(column), db.getUserInteractionWriteExecutor());

                }, workExecutor)
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    /// Analyzes [SelectionOption]s to check whether they are intended to get deleted, inserted or updated
    ///
    /// @return CrudItems
    private CompletableFuture<CrudItems<SelectionOption>> analyzeSelectionOptions(@NonNull FullColumn fullColumn) {
        return supplyAsync(() -> db.getSelectionOptionDao().getSelectionOptionIds(fullColumn.getColumn().getId()), db.getUserInteractionReadExecutor())
                .thenApplyAsync(selectionOptionIds -> {
                    final var toUpdate = new ArrayList<SelectionOption>();
                    final var toInsert = new ArrayList<SelectionOption>();
                    final var newSelectionOptions = fullColumn.getSelectionOptions();

                    for (final var newSelectionOption : newSelectionOptions) {

                        if (newSelectionOption.getId() == 0L) {
                            newSelectionOption.setColumnId(fullColumn.getColumn().getId());
                            toInsert.add(newSelectionOption);

                        } else {
                            if (selectionOptionIds.contains(newSelectionOption.getId())) {
                                toUpdate.add(newSelectionOption);
                            }
                        }

                    }

                    final var toDelete = selectionOptionIds
                            .stream()
                            .filter(id -> newSelectionOptions
                                    .stream()
                                    .noneMatch(selectionOption -> Objects.equals(selectionOption.getId(), id)))
                            .collect(Collectors.toSet());

                    return new CrudItems<>(toDelete, toUpdate, toInsert);
                }, workExecutor);
    }

    private record CrudItems<T>(
            @NonNull Collection<Long> toDelete,
            @NonNull Collection<T> toUpdate,
            @NonNull Collection<T> toInsert
    ) {
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> reorderColumn(@NonNull Account account,
                                                 long tableId,
                                                 @NonNull List<Long> newColumnOrder) {
        return supplyAsync(() -> db.getColumnDao().getNotDeletedOrderWeights(tableId), db.getUserInteractionReadExecutor())
                .thenApplyAsync(originalOrderWeights -> {

                    final var newOrderWeights = columnReorderUtil.reorderColumns(originalOrderWeights, newColumnOrder);
                    final var newOrderWeightsDiff = columnReorderUtil.filterChanged(originalOrderWeights, newOrderWeights);

                    for (final var entry : newOrderWeightsDiff.entrySet()) {
                        db.getColumnDao().updateOrderWeight(entry.getKey(), entry.getValue());
                    }

                    return account;

                }, db.getUserInteractionWriteExecutor())
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteColumn(@NonNull Table table, @NonNull Column column) {
        return supplyAsync(() -> {

            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.MANAGE));
            }

            column.setStatus(DBStatus.LOCAL_DELETED);

            return column;

        }, workExecutor)
                .thenAcceptAsync(db.getColumnDao()::update, db.getUserInteractionWriteExecutor())
                .thenApplyAsync(v -> db.getAccountDao().getAccountById(column.getAccountId()), db.getUserInteractionReadExecutor())
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> createRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<FullData> fullDataSet) {
        return supplyAsync(() -> {

            if (!table.hasManagePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.CREATE));
            }

            row.setStatus(DBStatus.LOCAL_EDITED);
            row.setAccountId(account.getId());

            return row;

        }, workExecutor)
                .thenApplyAsync(db.getRowDao()::insert, db.getUserInteractionWriteExecutor())
                .thenAcceptAsync(row::setId, workExecutor)
                .thenRunAsync(() -> {

                    for (final var fullData : fullDataSet) {
                        // In case of cloning
                        fullData.getData().setId(0L);
                        fullData.getData().setRowId(row.getId());
                        final var insertedDataId = db.getDataDao().insert(fullData.getData());
                        fullData.getData().setId(insertedDataId);

                        if (fullData.getDataType().hasLinkValue()) {

                            Optional.ofNullable(fullData.getLinkValueWithProviderRemoteId())
                                    .map(LinkValueWithProviderId::getLinkValue)
                                    .map(linkValue -> {
                                        linkValue.setDataId(insertedDataId);
                                        return linkValue;
                                    })
                                    .ifPresent(db.getLinkValueDao()::insertLinkValueAndUpdateData);

                        }

                        if (fullData.getDataType().hasSelectionOptions()) {

                            for (final var selectionOption : fullData.getSelectionOptions()) {
                                final var crossRef = new DataSelectionOptionCrossRef(insertedDataId, selectionOption.getId());
                                db.getDataSelectionOptionCrossRefDao().upsert(crossRef);
                            }

                        }
                    }
                    logger.info("PERF :: " + "Adding row " + row.getId());

                }, db.getUserInteractionWriteExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> updateRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<FullData> fullDataSet) {
        return supplyAsync(() -> {

            if (!table.hasUpdatePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.UPDATE));
            }

            row.setStatus(DBStatus.LOCAL_EDITED);
            row.setAccountId(account.getId());

            return row;

        }, workExecutor)
                .thenAcceptAsync(db.getRowDao()::update, db.getUserInteractionWriteExecutor())
                .thenComposeAsync(v -> updateData(fullDataSet, row), workExecutor)
                .thenAcceptAsync(v -> db.getDataDao().deleteRowIfEmpty(row.getId()), db.getUserInteractionWriteExecutor())
                .thenApplyAsync(v -> account, workExecutor)
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @NonNull
    private CompletableFuture<Void> updateData(@NonNull Collection<FullData> fullDataSet,
                                               @NonNull Row row) {
        return completedFuture(fullDataSet)
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(stream -> stream.map(fullData -> runAsync(() -> fullData.getData().setRowId(row.getId()), workExecutor)
                        .thenApplyAsync(v -> db.getDataDao().exists(
                                fullData.getData().getColumnId(),
                                fullData.getData().getRowId()), db.getUserInteractionReadExecutor())
                        .thenApplyAsync(exists -> {
                            if (exists) {
                                db.getDataDao().update(fullData.getData());

                            } else {
                                final var insertedDataId = db.getDataDao().insert(fullData.getData());
                                fullData.getData().setId(insertedDataId);
                            }

                            return fullData;
                        }, db.getUserInteractionWriteExecutor())
                        .thenComposeAsync(this::updateSelectionOptionCrossRefs, workExecutor)
                        .thenComposeAsync(fd -> this.updateLinkValue(fd, row.getAccountId()), workExecutor)
                ), workExecutor)
                .thenApplyAsync(completableFutureStream -> completableFutureStream.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor);
    }

    @NonNull
    private CompletableFuture<FullData> updateSelectionOptionCrossRefs(@NonNull FullData fullData) {
        if (!fullData.getDataType().hasSelectionOptions()) {
            return completedFuture(fullData);
        }

        return completedFuture(fullData)
                .thenApplyAsync(res -> db.getDataSelectionOptionCrossRefDao().getCrossRefs(fullData.getData().getId()), db.getUserInteractionReadExecutor())
                .thenApplyAsync(storedCrossRefs -> {

                    final var editedCrossRefs = fullData.getSelectionOptions().stream()
                            .map(selectionOption -> new DataSelectionOptionCrossRef(fullData.getData().getId(), selectionOption.getId()))
                            .collect(toUnmodifiableSet());

                    final var toAdd = editedCrossRefs
                            .stream()
                            .filter(not(storedCrossRefs::contains))
                            .collect(toUnmodifiableSet());

                    final var toDelete = storedCrossRefs
                            .stream()
                            .filter(not(editedCrossRefs::contains))
                            .collect(toUnmodifiableSet());

                    return new Pair<>(toAdd, toDelete);

                }, workExecutor)
                .thenApplyAsync(args -> {

                    for (final var toAdd : args.first) {
                        db.getDataSelectionOptionCrossRefDao().insert(toAdd);
                    }

                    for (final var toDelete : args.second) {
                        db.getDataSelectionOptionCrossRefDao().delete(toDelete);
                    }

                    return fullData;

                }, db.getUserInteractionWriteExecutor());
    }

    @NonNull
    private CompletableFuture<FullData> updateLinkValue(@NonNull FullData fullData, long accountId) {
        if (!fullData.getDataType().hasLinkValue()) {
            return completedFuture(fullData);
        }

        final var linkValueWithProviderId = Optional
                .of(fullData)
                .map(FullData::getLinkValueWithProviderRemoteId);

        final var optionalLinkValue = linkValueWithProviderId
                .map(LinkValueWithProviderId::getLinkValue);

        final var providerId = linkValueWithProviderId
                .map(LinkValueWithProviderId::getProviderId);

        if (optionalLinkValue.isPresent()) {
            final var linkValue = optionalLinkValue.get();
            linkValue.setDataId(fullData.getData().getId());

            return supplyAsync(() -> db.getSearchProviderDao().getSearchProviderId(accountId, providerId.orElse(null)), db.getUserInteractionReadExecutor())
                    .thenAcceptAsync(linkValue::setProviderId, workExecutor)
                    .thenRunAsync(() -> db.getLinkValueDao().upsertLinkValueAndUpdateData(linkValue), db.getUserInteractionWriteExecutor())
                    .thenApplyAsync(v -> {
                        fullData.getData().getValue().setLinkValueRef(fullData.getData().getId());
                        return fullData;
                    }, workExecutor);

        } else {
            return runAsync(() -> db.getLinkValueDao().delete(fullData.getData().getId()), db.getUserInteractionWriteExecutor())
                    .thenApplyAsync(v -> {
                        fullData.getData().getValue().setLinkValueRef(null);
                        return fullData;
                    }, workExecutor);
        }
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteRow(@NonNull Table table, @NonNull Row row) {
        return supplyAsync(() -> {

            if (!table.hasDeletePermission()) {
                throw new CompletionException(new InsufficientPermissionException(EPermissionV2Dto.DELETE));
            }

            row.setStatus(DBStatus.LOCAL_DELETED);

            return row;

        }, workExecutor)
                .thenAcceptAsync(db.getRowDao()::update, db.getUserInteractionWriteExecutor())
                .thenApplyAsync(v -> db.getAccountDao().getAccountById(row.getAccountId()), db.getUserInteractionReadExecutor())
                .thenAcceptAsync(this::schedulePush, workExecutor);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<List<FullColumn>> getNotDeletedColumns(@NonNull Table table) {
        return supplyAsync(() -> db.getColumnDao().getNotDeletedColumns(table.getId()), db.getUserInteractionReadExecutor());
    }

    @AnyThread
    public CompletableFuture<Map<Long, FullData>> getRawColumnIdAndFullData(long rowId) {
        return supplyAsync(() -> db.getDataDao().getColumnIdAndFullData(rowId), db.getUserInteractionReadExecutor());
    }

    /// @noinspection UnusedReturnValue
    @AnyThread
    public CompletableFuture<Void> updateCurrentRow(long tableId, @NonNull Long currentRowId) {
        return runAsync(() -> db.getTableDao().updateCurrentRow(tableId, currentRowId), db.getUserInteractionWriteExecutor());
    }
}
