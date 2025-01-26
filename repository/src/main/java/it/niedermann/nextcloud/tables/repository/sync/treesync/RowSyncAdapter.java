package it.niedermann.nextcloud.tables.repository.sync.treesync;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.DataSelectionOptionCrossRef;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeCollectionV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.FetchAndPutRowV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.CreateRowResponseV2Mapper;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;

class RowSyncAdapter extends AbstractSyncAdapter<Table> {

    private static final String TAG = RowSyncAdapter.class.getSimpleName();
    private final FetchAndPutRowV1Mapper fetchRowV1Mapper;
    private final CreateRowResponseV2Mapper createRowV2Mapper;

    public RowSyncAdapter(@NonNull Context context) {
        this(context, null);
    }

    private RowSyncAdapter(@NonNull Context context,
                           @Nullable SyncStatusReporter reporter) {
        this(context, reporter,
                new FetchAndPutRowV1Mapper(),
                new CreateRowResponseV2Mapper());
    }

    private RowSyncAdapter(@NonNull Context context,
                           @Nullable SyncStatusReporter reporter,
                           @NonNull FetchAndPutRowV1Mapper fetchRowV1Mapper,
                           @NonNull CreateRowResponseV2Mapper createRowV2Mapper) {
        super(context, reporter);
        this.fetchRowV1Mapper = fetchRowV1Mapper;
        this.createRowV2Mapper = createRowV2Mapper;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalCreations(@NonNull Account account, @NonNull Table table) {
        return supplyAsync(() -> db.getRowDao().getLocallyCreatedRows(account.getId(), table.getId()), db.getParallelExecutor())
                .thenComposeAsync(fullRowsToUpdate -> {
                    Log.v(TAG, "------ Pushing " + fullRowsToUpdate.size() + " local row creations for " + account.getAccountName());

                    final var version = account.getTablesVersion();

                    if (version == null) {
                        throw new IllegalStateException(TablesVersion.class.getSimpleName() + " is null. Capabilities need to be synchronized before pushing local changes.");
                    }

                    return allOf(fullRowsToUpdate.stream()
                            .peek(fullRow -> Log.i(TAG, "------ → PUT/POST: " + fullRow.getRow().getId()))
                            .map(fullRow -> {
                                final var createRowDto = createRowV2Mapper.toCreateRowV2Dto(version, fullRow.getFullData());
                                // TODO maybe preload map of table local / remote IDs?
                                return supplyAsync(() -> db.getTableDao().getRemoteId(fullRow.getRow().getTableId()), db.getParallelExecutor())
                                        .thenComposeAsync(tableRemoteId -> requestHelper.executeNetworkRequest(account, apis -> apis.apiV2().createRow(ENodeCollectionV2Dto.TABLES, tableRemoteId, createRowDto)), workExecutor)
                                        .thenComposeAsync(response -> {
                                            if (response.isSuccessful()) {
                                                fullRow.getRow().setStatus(DBStatus.VOID);
                                                final var body = response.body();

                                                if (body == null || body.ocs == null || body.ocs.data == null) {
                                                    throw new NullPointerException("Pushing changes for row with local ID " + fullRow.getRow().getId() + " was successfully, but response body was empty");
                                                }

                                                fullRow.getRow().setRemoteId(body.ocs.data.remoteId());
                                                return runAsync(() -> db.getRowDao().update(fullRow.getRow()), db.getSequentialExecutor());

                                            } else {
                                                serverErrorHandler.responseToException(response, "Could not push local row creations for " + fullRow.getRow().getId(), false).ifPresent(this::throwError);
                                                return completedFuture(null);
                                            }
                                        });
                            }).toArray(CompletableFuture[]::new));
                }, workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalUpdates(@NonNull Account account, @NonNull Table table) {
        return supplyAsync(() -> db.getRowDao().getLocallyEditedRows(account.getId(), table.getId()), db.getParallelExecutor())
                .thenComposeAsync(fullRowsToUpdate -> {
                    Log.v(TAG, "------ Pushing " + fullRowsToUpdate.size() + " local row updates for " + account.getAccountName());

                    final var version = account.getTablesVersion();

                    if (version == null) {
                        throw new IllegalStateException(TablesVersion.class.getSimpleName() + " is null. Capabilities need to be synchronized before pushing local changes.");
                    }

                    return allOf(fullRowsToUpdate.stream()
                            .peek(fullRow -> Log.i(TAG, "------ → PUT/POST: " + fullRow.getRow().getRemoteId()))
                            .map(fullRow -> {
                                final var updateRowDto = fetchRowV1Mapper.toJsonElement(version, fullRow.getFullData());
                                return requestHelper.executeNetworkRequest(account, apis -> apis.apiV1().updateRow(requireNonNull(fullRow.getRow().getRemoteId()), updateRowDto))
                                        .thenComposeAsync(response -> {
                                            Log.i(TAG, "------ → HTTP " + response.code());

                                            if (response.isSuccessful()) {
                                                fullRow.getRow().setStatus(DBStatus.VOID);
                                                final var body = response.body();
                                                if (body == null) {
                                                    throw new NullPointerException("Pushing changes for row " + fullRow.getRow().getRemoteId() + " was successfully, but response body was empty");
                                                }

                                                fullRow.getRow().setRemoteId(body.remoteId());
                                                return runAsync(() -> db.getRowDao().update(fullRow.getRow()), db.getSequentialExecutor());

                                            } else {
                                                serverErrorHandler.responseToException(response, "Could not push local changes for row " + fullRow.getRow().getRemoteId(), false).ifPresent(this::throwError);
                                                return completedFuture(null);
                                            }
                                        });
                            }).toArray(CompletableFuture[]::new));
                }, workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalDeletions(@NonNull Account account, @NonNull Table table) {
        return supplyAsync(() -> db.getRowDao().getLocallyDeletedRows(account.getId(), table.getId()), db.getParallelExecutor())
                .thenApplyAsync(rows -> {
                    Log.v(TAG, "------ Pushing " + rows.size() + " local row deletions for " + account.getAccountName());

                    return rows.stream()
                            .peek(row -> Log.i(TAG, "------ → DELETE: " + row.getRemoteId()))
                            .map(row -> {
                                if (row.getRemoteId() == null) {
                                    return runAsync(() -> db.getRowDao().delete(row), db.getSequentialExecutor());

                                } else {
                                    return requestHelper.executeNetworkRequest(account, apis -> apis.apiV1().deleteRow(row.getRemoteId()))
                                            .thenComposeAsync(response -> {
                                                Log.i(TAG, "------ → HTTP " + response.code());
                                                if (response.isSuccessful()) {
                                                    return runAsync(() -> db.getRowDao().delete(row), db.getSequentialExecutor());
                                                } else {
                                                    serverErrorHandler.responseToException(response, "Could not delete row " + row.getRemoteId(), false).ifPresent(this::throwError);
                                                    return completedFuture(null);
                                                }
                                            });
                                }
                            });
                }, workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account,
                                                     @NonNull Table table) {
        return supplyAsync(() -> new Pair<>(
                db.getColumnDao().getNotDeletedColumnRemoteIdsAndFullColumns(table.getId()),
                db.getColumnDao().getNotDeletedSelectionOptions(table.getId())
        ), db.getParallelExecutor())
                .thenComposeAsync(columnsAndIdMap -> {
                    final var columnRemoteAndLocalIds = columnsAndIdMap.first
                            .entrySet()
                            .stream()
                            .collect(toMap(Map.Entry::getKey, entry -> requireNonNull(entry.getValue().getColumn().getRemoteId())));

                    return fetchAndPersistRows(
                            account, table, 0,
                            ConcurrentHashMap.newKeySet(columnsAndIdMap.first.size()),
                            columnsAndIdMap.first,
                            columnRemoteAndLocalIds,
                            columnsAndIdMap.second);
                }, workExecutor)
                .thenApplyAsync(fetchedRowIds -> new Pair<>(fetchedRowIds, db.getRowDao().getIds(table.getId())), db.getParallelExecutor())
                .thenApplyAsync(pair -> {
                    final var fetchedRowIds = pair.first;
                    final var existingRowIds = pair.second;

                    final var rowIdsToDelete = new HashSet<>(existingRowIds);
                    rowIdsToDelete.removeAll(fetchedRowIds);
                    Log.i(TAG, "------ ← Delete rows with local ID in " + rowIdsToDelete);

                    return rowIdsToDelete;
                }, workExecutor)
                .thenAcceptAsync(existingRowIds -> existingRowIds.forEach(db.getRowDao()::delete), db.getSequentialExecutor());
    }

    @NonNull
    public CompletableFuture<FullRow> upsertRow(@NonNull final FullRow fullRow,
                                                @Nullable Long potentialRowId) {
        return supplyAsync(fullRow::getRow, workExecutor)
                .thenComposeAsync(row -> {

                    if (potentialRowId == null) {

                        Log.i(TAG, "------ ← Adding row " + row.getRemoteId() + " to database");
                        return supplyAsync(() -> db.getRowDao().insert(row), db.getSequentialExecutor())
                                .thenAcceptAsync(row::setId, workExecutor)
                                .thenApplyAsync(v -> row.getId(), workExecutor);

                    } else {

                        row.setId(potentialRowId);

                        Log.i(TAG, "------ ← Updating row " + row.getRemoteId() + " in database");
                        return runAsync(() -> db.getRowDao().update(row), db.getSequentialExecutor())
                                .thenApplyAsync(v -> potentialRowId, workExecutor);
                    }

                }, workExecutor)
                .thenAcceptAsync(actualRowId -> fullRow.getFullData().stream()
                        .map(FullData::getData)
                        .forEach(data -> data.setRowId(actualRowId)), workExecutor)
                .thenApplyAsync(actualRowId -> fullRow, workExecutor);
    }

    /// @return Collection of [Row#id]s that have been fetched and persisted
    @NonNull
    private CompletableFuture<Collection<Long>> fetchAndPersistRows(@NonNull final Account account,
                                                                    @NonNull final Table table,
                                                                    final int offset,
                                                                    @NonNull final Collection<Long> target,
                                                                    @NonNull final Map<Long, FullColumn> columnRemoteIdsToFullColumns,
                                                                    @NonNull final Map<Long, Long> columnRemoteIdsToColumnLocalIds,
                                                                    @NonNull final Map<Long, List<SelectionOption>> columnRemoteIdToSelectionOptions) {

        return checkRemoteIdNotNull(table.getRemoteId())
                .thenComposeAsync(tableRemoteId -> requestHelper.executeNetworkRequest(account, apis -> apis.apiV1().getRows(tableRemoteId, TablesV1API.DEFAULT_API_LIMIT_ROWS, offset)), workExecutor)
                .thenComposeAsync(response -> switch (response.code()) {
                    case 200: {
                        final var rowDtos = response.body();

                        if (rowDtos == null) {
                            throw new RuntimeException("Response body is null");
                        }

                        yield allOf(rowDtos.stream().map(rowDto -> supplyAsync(() -> {
                            final var fullRow = fetchRowV1Mapper.toEntity(account.getId(), rowDto, columnRemoteIdsToFullColumns, columnRemoteIdToSelectionOptions, requireNonNull(account.getTablesVersion()));

                            final var row = fullRow.getRow();
                            row.setAccountId(table.getAccountId());
                            row.setTableId(table.getId());
                            row.setETag(response.headers().get(HEADER_ETAG));

                            return fullRow;
                        }, workExecutor)
                                .thenApplyAsync(fullRow -> new Pair<>(fullRow, Optional.of(fullRow)
                                        .map(FullRow::getRow)
                                        .map(Row::getRemoteId)
                                        .map(remoteId -> db.getRowDao().getRowId(table.getId(), remoteId))
                                ), db.getParallelExecutor())
                                .thenComposeAsync(rowAndRowId -> upsertRow(rowAndRowId.first, rowAndRowId.second.orElse(null)), workExecutor)
                                .thenComposeAsync(fullRow -> allOf(fullRow
                                        .getFullData().stream()
                                        .map(fullData -> upsertData(account.getId(), fullData, columnRemoteIdsToColumnLocalIds))
                                        .toArray(CompletableFuture[]::new))
                                        .thenComposeAsync(v -> {
                                            target.add(fullRow.getRow().getId());
                                            return CompletableFuture.<Void>completedFuture(null);
                                        }, workExecutor), workExecutor)).toArray(CompletableFuture[]::new))
                                .thenComposeAsync(v -> {
                                    if (rowDtos.size() < TablesV1API.DEFAULT_API_LIMIT_ROWS) {
                                        return completedFuture(target);
                                    }

                                    final var newOffset = offset + rowDtos.size();
                                    return fetchAndPersistRows(account, table, newOffset, target, columnRemoteIdsToFullColumns, columnRemoteIdsToColumnLocalIds, columnRemoteIdToSelectionOptions);
                                }, workExecutor);
                    }

                    default: {
                        final var future = new CompletableFuture<Collection<Long>>();
                        serverErrorHandler
                                .responseToException(response, "Could not fetch rows for table with remote ID " + table.getRemoteId(), true)
                                .ifPresentOrElse(
                                        future::completeExceptionally,
                                        () -> future.complete(target));
                        yield future;
                    }
                }, workExecutor);
    }

    @NonNull
    public CompletableFuture<Void> upsertData(long accountId,
                                              @NonNull final FullData fullData,
                                              @NonNull final Map<Long, Long> columnRemoteAndLocalIds) {
        final var data = fullData.getData();

        if (!columnRemoteAndLocalIds.containsKey(data.getRemoteColumnId())) {
            // Data deletion is handled by database constraints
            Log.w(TAG, "------ Could not find remoteColumnId " + data.getRemoteColumnId() + ". Probably this column has been deleted but its data is still being responded by the server (See https://github.com/nextcloud/tables/issues/257)");
            return completedFuture(null);
        }

        return supplyAsync(() -> db.getDataDao().getDataIdForCoordinates(data.getRemoteColumnId(), data.getRowId()), db.getParallelExecutor())
                .thenApplyAsync(Optional::ofNullable, workExecutor)
                .thenComposeAsync(existingDataId -> {
                    existingDataId.ifPresent(data::setId);

                    final var dataType = fullData.getDataType();

                    return completedFuture(null)

                            .thenComposeAsync(v -> dataType.hasLinkValue()
                                    ? resolveLinkValueRef(accountId, fullData)
                                    : completedFuture(null), workExecutor)

                            .thenComposeAsync(v -> existingDataId.isPresent()
                                    ? runAsync(() -> db.getDataDao().update(data), db.getSequentialExecutor())
                                    : supplyAsync(() -> db.getDataDao().insert(data), db.getSequentialExecutor())
                                    .thenAcceptAsync(data::setId, workExecutor)
                                    .thenComposeAsync(v2 -> dataType.hasSelectionOptions()
                                            ? insertDependingLinkValues(fullData)
                                            : completedFuture(null), workExecutor))

                            .thenComposeAsync(v -> dataType.hasSelectionOptions()
                                    ? updateSelectionOptionsCrossRefs(fullData)
                                    : completedFuture(null), workExecutor)

                            .thenComposeAsync(v -> dataType.hasUserGroups()
                                    ? updateUserGroupsCrossRefs(fullData)
                                    : completedFuture(null), workExecutor);

                }, workExecutor);
    }

    @NonNull
    private CompletableFuture<Void> updateSelectionOptionsCrossRefs(@NonNull FullData fullData) {
        return completedFuture(null)
                .thenApplyAsync(v -> db.getDataSelectionOptionCrossRefDao().getCrossRefs(fullData.getData().getId()), db.getParallelExecutor())
                .thenApplyAsync(crossRefs -> new Pair<>(fullData.getSelectionOptions().stream()
                        .map(selectionOption -> new DataSelectionOptionCrossRef(fullData.getData().getId(), selectionOption.getId()))
                        .collect(toUnmodifiableSet()), crossRefs), workExecutor)
                .thenApplyAsync(args -> {

                    final var fetchedCrossRefs = args.first;
                    final var storedCrossRefs = args.second;

                    final var toAdd = fetchedCrossRefs
                            .stream()
                            .filter(not(storedCrossRefs::contains))
                            .collect(toUnmodifiableSet());

                    final var toDelete = storedCrossRefs
                            .stream()
                            .filter(not(fetchedCrossRefs::contains))
                            .collect(toUnmodifiableSet());

                    return new Pair<>(toAdd, toDelete);

                }, workExecutor)
                .thenAcceptAsync(args -> {

                    for (final var toAdd : args.first) {
                        db.getDataSelectionOptionCrossRefDao().insert(toAdd);
                    }

                    for (final var toDelete : args.second) {
                        db.getDataSelectionOptionCrossRefDao().delete(toDelete);
                    }

                }, db.getSequentialExecutor());
    }

    @NonNull
    private CompletableFuture<Void> updateUserGroupsCrossRefs(@NonNull FullData fullData) {
        // TODO implement
        return completedFuture(null);
    }

    @NonNull
    private CompletableFuture<Void> resolveLinkValueRef(long accountId, @NonNull FullData fullData) {
        final var dataId = fullData.getData().getId();
        final var linkValueWithProviderRemoteId = fullData.getLinkValueWithProviderRemoteId();

        // Link has been deleted remote, let's delete it physically.
        // Probably not necessary, because the Data object will get deleted too and the Foreign Key constraints will ensure the LinkValue gets deleted, too.
        if (linkValueWithProviderRemoteId == null) {
            fullData.getData().getValue().setLinkValueRef(null);
            return runAsync(() -> db.getLinkValueDao().delete(dataId), db.getSequentialExecutor());
        }

        final var searchProviderRemoteId = linkValueWithProviderRemoteId.getProviderId();

        return supplyAsync(() -> db.getSearchProviderDao().getSearchProviderId(accountId, searchProviderRemoteId), db.getParallelExecutor())
                .thenApplyAsync(searchProviderId -> {
                    fullData.getData().getValue().setLinkValueRef(dataId > 0 ? dataId : null);

                    final var linkValue = fullData.getLinkValueWithProviderRemoteId().getLinkValue();
                    linkValue.setDataId(dataId);
                    linkValue.setProviderId(searchProviderId);
                    return linkValue;
                }, workExecutor)
                .thenAcceptAsync(linkValue -> {
                    // Prevent Foreign Key Exception in case LinkValue.getDataId does not exist yet
                    if (linkValue.getDataId() > 0) {
                        db.getLinkValueDao().upsert(linkValue);
                    }
                }, db.getSequentialExecutor());
    }

    @NonNull
    private CompletableFuture<Void> insertDependingLinkValues(@NonNull FullData fullData) {
        return completedFuture(fullData.getLinkValueWithProviderRemoteId())
                .thenComposeAsync(linkValueWithProviderRemoteId -> {

                    // We are only looking for cases where data gets inserted, not deleted
                    if (linkValueWithProviderRemoteId == null) {
                        return completedFuture(null);
                    }

                    final var linkValue = linkValueWithProviderRemoteId.getLinkValue();
                    final var dataId = fullData.getData().getId();
                    linkValue.setDataId(dataId);

                    return runAsync(() -> {
                        db.getLinkValueDao().upsert(linkValue);
                        db.getDataDao().update(fullData.getData());
                    }, db.getSequentialExecutor());

                }, workExecutor);
    }
}