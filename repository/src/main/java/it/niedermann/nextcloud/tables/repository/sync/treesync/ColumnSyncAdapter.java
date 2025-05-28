package it.niedermann.nextcloud.tables.repository.sync.treesync;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.DefaultValueSelectionOptionCrossRef;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.remote.shared.model.RemoteDto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnRequestV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UpdateColumnResponseV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.remote.tablesV2.creators.DataTypeCreatorServiceRegistry;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.CreateColumnResponseV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.NonNullRemoteMapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.ColumnRequestV1Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.ColumnV2Mapper;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;
import retrofit2.Response;

class ColumnSyncAdapter extends AbstractSyncAdapter<Table> {

    private static final Logger logger = Logger.getLogger(ColumnSyncAdapter.class.getSimpleName());

    private final DataTypeServiceRegistry<ColumnCreator> columnCreator;
    private final NonNullRemoteMapper<ColumnV2Dto, FullColumn> columnRequestMapper;
    private final Function<FullColumn, ColumnRequestV1Dto> columnRequestV1Mapper;

    public ColumnSyncAdapter(@NonNull Context context,
                             @Nullable SyncStatusReporter reporter) {
        this(context, reporter,
                new DataTypeCreatorServiceRegistry(),
                new ColumnV2Mapper(),
                new ColumnRequestV1Mapper());
    }

    private ColumnSyncAdapter(@NonNull Context context,
                              @Nullable SyncStatusReporter reporter,
                              @NonNull DataTypeServiceRegistry<ColumnCreator> columnCreator,
                              @NonNull NonNullRemoteMapper<ColumnV2Dto, FullColumn> columnRequestMapper,
                              @NonNull Function<FullColumn, ColumnRequestV1Dto> columnRequestV1Mapper) {
        super(context, reporter);
        this.columnCreator = columnCreator;
        this.columnRequestMapper = columnRequestMapper;
        this.columnRequestV1Mapper = columnRequestV1Mapper;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalCreations(@NonNull Account account, @NonNull Table table) {
        return supplyAsync(() -> db.getColumnDao().getLocallyCreatedColumns(account.getId(), table.getId()), db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(columnsToCreate -> columnsToCreate
                        .map(fullColumn -> completedFuture(null)
                                .thenComposeAsync(v -> this.createRemote(account, table, fullColumn), workExecutor)
                                .thenComposeAsync(response -> this.markLocallyAsCreated(fullColumn, response), workExecutor)
                        ), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor);
    }

    @NonNull
    private CompletableFuture<Response<OcsResponse<CreateColumnResponseV2Dto>>> createRemote(@NonNull Account account, @NonNull Table table, @NonNull FullColumn fullColumn) {
        return checkRemoteIdNull(fullColumn.getColumn().getRemoteId())
                .thenComposeAsync(v -> requestHelper.executeTablesV2Request(account, api -> columnCreator.getService(fullColumn.getColumn().getDataType())
                        .createColumn(
                                api,
                                requireNonNull(table.getRemoteId()),
                                columnRequestMapper.toDto(fullColumn))), workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalUpdates(@NonNull Account account, @NonNull Table table) {
        return supplyAsync(() -> db.getColumnDao().getLocallyEditedColumns(account.getId(), table.getId()), db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(columnsToUpdate -> columnsToUpdate
                        .map(fullColumn -> completedFuture(null)
                                .thenComposeAsync(v -> this.updateRemote(account, table, fullColumn), workExecutor)
                                .thenComposeAsync(response -> this.markLocallyAsUpdated(fullColumn, response), workExecutor)
                        ), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new), workExecutor)
                .thenComposeAsync(CompletableFuture::allOf, workExecutor);
    }

    @NonNull
    private CompletableFuture<Response<UpdateColumnResponseV1Dto>> updateRemote(@NonNull Account account, @NonNull Table table, @NonNull FullColumn fullColumn) {
        final var remoteId = fullColumn.getColumn().getRemoteId();
        return checkRemoteIdNotNull(remoteId)
                .thenComposeAsync(v -> requestHelper.executeTablesV1Request(account, api -> api.updateColumn(
                        requireNonNull(remoteId),
                        columnRequestV1Mapper.apply(fullColumn))), workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalDeletions(@NonNull Account account, @NonNull Table table) {
        return supplyAsync(() -> db.getColumnDao().getLocallyDeletedColumns(account.getId(), table.getId()), db.getSyncReadExecutor())
                .thenApplyAsync(Collection::stream, workExecutor)
                .thenApplyAsync(columnToDelete -> columnToDelete
                        .peek(fullColumn -> logger.info(() -> "--- → DELETE: " + fullColumn.getColumn().getTitle()))
                        .map(fullColumn -> fullColumn.getColumn().getRemoteId() == null
                                ? runAsync(() -> db.getColumnDao().delete(fullColumn.getColumn()), db.getSyncWriteExecutor())
                                : deleteRemote(account, table, fullColumn)
                                .thenComposeAsync(response -> this.deleteLocallyPhysically(fullColumn.getColumn(), response), workExecutor)
                        ), workExecutor)
                .thenApplyAsync(completableFutures -> completableFutures.toArray(CompletableFuture[]::new))
                .thenComposeAsync(CompletableFuture::allOf, workExecutor);
    }

    private @NonNull CompletableFuture<Response<RemoteDto>> deleteRemote(@NonNull Account account, @NonNull Table table, @NonNull FullColumn fullColumn) {
        final var remoteId = fullColumn.getColumn().getRemoteId();
        return checkRemoteIdNotNull(remoteId)
                .thenComposeAsync(v -> requestHelper.executeTablesV1Request(account, api -> api.deleteColumn(requireNonNull(remoteId))), workExecutor);
    }

    @NonNull
    private CompletableFuture<Void> markLocallyAsCreated(@NonNull FullColumn entity, @NonNull Response<OcsResponse<CreateColumnResponseV2Dto>> response) {
        logger.info(() -> "-→ HTTP " + response.code());
        if (response.isSuccessful()) {

            final var body = response.body();

            if (body == null || body.ocs == null || body.ocs.data == null) {
                throwError(new NullPointerException("Pushing changes for column " + entity + " was successful, but response body was empty"));
            }

            assert body != null;

            entity.getColumn().setRemoteId(body.ocs.data.remoteId());
            entity.getColumn().setStatus(DBStatus.VOID);

            return runAsync(() -> {
                db.getColumnDao().update(entity.getColumn());
                db.getDataDao().updateColumnRemoteIds(entity.getColumn().getId(), requireNonNull(entity.getColumn().getRemoteId()));
            }, db.getSyncWriteExecutor());

        } else {
            serverErrorHandler.responseToException(response, "Could not push local changes for column " + entity, false).ifPresent(this::throwError);
            return completedFuture(null);
        }
    }

    @NonNull
    private CompletableFuture<Void> markLocallyAsUpdated(@NonNull FullColumn entity, @NonNull Response<UpdateColumnResponseV1Dto> response) {
        logger.info(() -> "-→ HTTP " + response.code());
        if (response.isSuccessful()) {

            final var body = response.body();

            if (body == null) {
                throwError(new NullPointerException("Pushing changes for column " + entity + " was successful, but response body was empty"));
            }

            assert body != null;

            entity.getColumn().setRemoteId(body.remoteId());
            entity.getColumn().setStatus(DBStatus.VOID);

            return runAsync(() -> db.getColumnDao().update(entity.getColumn()), db.getSyncWriteExecutor());

        } else {
            serverErrorHandler.responseToException(response, "Could not push local changes for column " + entity, false).ifPresent(this::throwError);
            return completedFuture(null);
        }
    }

    private CompletableFuture<Void> deleteLocallyPhysically(@NonNull Column column, @NonNull Response<?> response) {
        return completedFuture(null)
                .thenComposeAsync(v -> {
                    logger.info(() -> "-→ HTTP " + response.code());

                    if (response.isSuccessful() || response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                        return runAsync(() -> db.getColumnDao().delete(column), db.getSyncWriteExecutor());

                    } else {
                        serverErrorHandler.responseToException(response, "Could not delete column " + column, false).ifPresent(this::throwError);
                        return completedFuture(null);
                    }
                }, workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account,
                                                     @NonNull Table table) {
        //noinspection SwitchStatementWithTooFewBranches
        return checkRemoteIdNotNull(table.getRemoteId())
                .thenComposeAsync(tableRemoteId -> requestHelper.executeTablesV2Request(account, api -> api.getColumns(ENodeTypeV2Dto.TABLE, tableRemoteId)), workExecutor)
                .thenComposeAsync(response -> switch (response.code()) {
                    case 200 -> {
                        final var responseBody = response.body();

                        if (responseBody == null || responseBody.ocs == null || responseBody.ocs.data == null) {
                            throw new RuntimeException("Response body is null");
                        }

                        final var columnDtos = responseBody.ocs.data;
                        final var columnRemoteIds = columnDtos.stream()
                                .map(RemoteDto::remoteId)
                                .map(Objects::requireNonNull)
                                .collect(toUnmodifiableSet());

                        yield supplyAsync(() -> db.getColumnDao().getColumnRemoteAndLocalIds(table.getId(), columnRemoteIds), db.getSyncReadExecutor())
                                .thenComposeAsync(columnIds -> allOf(columnDtos.stream().map(columnDto -> {
                                    final var fullColumn = columnRequestMapper.toEntity(columnDto);
                                    final var column = fullColumn.getColumn();
                                    column.setAccountId(account.getId());
                                    column.setTableId(table.getId());
                                    column.setETag(response.headers().get(HEADER_ETAG));

                                    final CompletableFuture<?> columnUpdateFuture;
                                    final var columnId = columnIds.get(column.getRemoteId());
                                    if (columnId == null) {
                                        logger.info(() -> "--- ← Adding column " + column.getTitle() + " to database");
                                        columnUpdateFuture = supplyAsync(() -> db.getColumnDao().insert(column), db.getSyncWriteExecutor())
                                                .thenAcceptAsync(column::setId, workExecutor)
                                                .handleAsync(provideDebugContext(table, column), workExecutor);
                                    } else {
                                        column.setId(columnId);
                                        logger.info(() -> "--- ← Updating column " + column.getTitle() + " in database");
                                        columnUpdateFuture = runAsync(() -> db.getColumnDao().update(column), db.getSyncWriteExecutor())
                                                .handleAsync(provideDebugContext(table, column), workExecutor);
                                    }

                                    if (!column.getDataType().hasSelectionOptions()) {
                                        return columnUpdateFuture;
                                    }

                                    final var selectionOptions = fullColumn.getSelectionOptions();
                                    final var selectionOptionRemoteIds = selectionOptions
                                            .stream()
                                            .map(SelectionOption::getRemoteId)
                                            .filter(Objects::nonNull)
                                            .collect(toUnmodifiableSet());

                                    return columnUpdateFuture
                                            .thenApplyAsync(v -> db.getSelectionOptionDao().getSelectionOptionRemoteColumnAndLocalIds(column.getId(), selectionOptionRemoteIds), db.getSyncReadExecutor())
                                            .thenComposeAsync(selectionOptionIds -> allOf(selectionOptions.stream().map(selectionOption -> {
                                                selectionOption.setColumnId(column.getId());

                                                final var selectionOptionId = selectionOptionIds.get(selectionOption.getRemoteId());
                                                if (selectionOptionId == null) {
                                                    logger.info(() -> "----- ← Adding selection option " + selectionOption.getLabel() + " to database");
                                                    return supplyAsync(() -> db.getSelectionOptionDao().insert(selectionOption), db.getSyncWriteExecutor())
                                                            .thenAcceptAsync(selectionOption::setId, workExecutor)
                                                            .handleAsync(provideDebugContext(selectionOption), workExecutor);

                                                } else {
                                                    selectionOption.setId(selectionOptionId);
                                                    logger.info(() -> "----- ← Updating selection option " + selectionOption.getLabel() + " in database");
                                                    return runAsync(() -> db.getSelectionOptionDao().update(selectionOption), db.getSyncWriteExecutor())
                                                            .handleAsync(provideDebugContext(selectionOption), workExecutor);
                                                }
                                            }).toArray(CompletableFuture[]::new)), workExecutor)
                                            .thenRunAsync(() -> logger.info(() -> "----- ← Delete all selection options for column \"" + column + "\" except remoteId " + selectionOptionRemoteIds), workExecutor)
                                            .thenRunAsync(() -> db.getSelectionOptionDao().deleteExcept(table.getId(), selectionOptionRemoteIds), db.getSyncWriteExecutor())
                                            .thenApplyAsync(v -> db.getSelectionOptionDao().getSelectionOptionRemoteIdsAndLocalIds(column.getId()), db.getSyncReadExecutor())
                                            .thenApplyAsync(selectionOptionRemoteIdsToLocalIds -> {

                                                final var existingSelectionOptionRemoteIds = selectionOptionRemoteIdsToLocalIds.keySet();

                                                final var targetDefaultOptions = fullColumn.getDefaultSelectionOptions();
                                                final var targetDefaultOptionRemoteIds = targetDefaultOptions.stream()
                                                        .map(SelectionOption::getRemoteId)
                                                        .filter(Objects::nonNull)
                                                        .collect(toUnmodifiableSet());

                                                final var toInsert = targetDefaultOptionRemoteIds
                                                        .stream()
                                                        .filter(existingSelectionOptionRemoteIds::contains)
                                                        .map(selectionOptionRemoteIdsToLocalIds::get)
                                                        .filter(Objects::nonNull)
                                                        .map(selectionOptionId -> new DefaultValueSelectionOptionCrossRef(column.getId(), requireNonNull(selectionOptionId)))
                                                        .collect(toUnmodifiableSet());

                                                final var toDelete = existingSelectionOptionRemoteIds
                                                        .stream()
                                                        .filter(remoteId -> targetDefaultOptions
                                                                .stream()
                                                                .noneMatch(selectionOption -> Objects.equals(selectionOption.getRemoteId(), remoteId)))
                                                        .collect(toUnmodifiableSet());

                                                return new CrudItems<>(toDelete, toInsert);

                                            }, workExecutor)
                                            .thenAcceptAsync(crudItems -> {
                                                db.getDefaultValueSelectionOptionCrossRefDao().deleteExcept(column.getId(), crudItems.toDelete());
                                                crudItems.toInsert().forEach(db.getDefaultValueSelectionOptionCrossRefDao()::upsert);
                                            }, db.getSyncWriteExecutor())
                                            .handleAsync(provideDebugContext(table, fullColumn, selectionOptions), workExecutor);
                                }).toArray(CompletableFuture[]::new)), workExecutor)
                                .thenRunAsync(() -> logger.info(() -> "--- ← Delete all columns except remoteId " + columnRemoteIds), workExecutor)
                                .thenRunAsync(() -> db.getColumnDao().deleteExcept(table.getId(), columnRemoteIds));
                    }
                    default -> {
                        serverErrorHandler.responseToException(response, "At table remote ID: " + table.getRemoteId(), true).ifPresent(this::throwError);
                        yield completedFuture(null);
                    }
                });
    }

    private record CrudItems<T>(
            @NonNull Collection<Long> toDelete,
            @NonNull Collection<T> toInsert
    ) {
    }
}
