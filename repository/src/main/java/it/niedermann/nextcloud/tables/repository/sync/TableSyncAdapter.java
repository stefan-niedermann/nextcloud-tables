package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.TableV2Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2.TableV2Mapper;


class TableSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = TableSyncAdapter.class.getSimpleName();
    private final Mapper<TableV2Dto, Table> tableMapper;

    public TableSyncAdapter(@NonNull Context context) {
        super(context);
        this.tableMapper = new TableV2Mapper();
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalChanges(@NonNull Account account) {
        Log.v(TAG, "Pushing local changes for " + account.getAccountName());
        return supplyAsync(() -> db.getTableDao().getTables(account.getId(), DBStatus.LOCAL_DELETED), db.getParallelExecutor())
                .thenAcceptAsync(deletedTables -> CompletableFuture.allOf(deletedTables.stream().map(table -> {
                    Log.i(TAG, "→ DELETE: " + table.getTitle());
                    final var remoteId = table.getRemoteId();
                    if (remoteId == null) {
                        return supplyAsync(() -> {
                            db.getTableDao().delete(table);
                            return null;
                        }, db.getSequentialExecutor());
                    } else {
                        return executeNetworkRequest(account, apis -> apis.apiV2().deleteTable(table.getRemoteId()))
                                .thenComposeAsync(response -> {
                                    Log.i(TAG, "-→ HTTP " + response.code());

                                    if (response.isSuccessful()) {
                                        return supplyAsync(() -> {
                                            db.getTableDao().delete(table);
                                            return null;
                                        }, db.getSequentialExecutor());

                                    } else {
                                        serverErrorHandler.responseToException(response, "Could not delete table " + table.getTitle(), false).ifPresent(this::throwError);
                                        return CompletableFuture.completedFuture(null);
                                    }
                                }, workExecutor);
                    }
                }).toArray(CompletableFuture[]::new)), workExecutor)
                .thenApplyAsync(v -> db.getTableDao().getTables(account.getId(), DBStatus.LOCAL_EDITED), db.getParallelExecutor())
                .thenAcceptAsync(changedTables -> CompletableFuture.allOf(changedTables.stream().map(table -> {
                    Log.i(TAG, "→ PUT/POST: " + table.getTitle());

                    return executeNetworkRequest(account, apis -> table.getRemoteId() == null
                            ? apis.apiV2().createTable(table.getTitle(),
                            table.getDescription(),
                            table.getEmoji(),
                            TablesV2API.DEFAULT_TABLES_TEMPLATE)
                            : apis.apiV2().updateTable(table.getRemoteId(),
                            table.getTitle(),
                            table.getDescription(),
                            table.getEmoji()))
                            .thenComposeAsync(response -> {
                                Log.i(TAG, "-→ HTTP " + response.code());
                                if (response.isSuccessful()) {
                                    table.setStatus(DBStatus.VOID);
                                    final var body = response.body();
                                    if (body == null || body.ocs == null || body.ocs.data == null) {
                                        throwError(new NullPointerException("Pushing changes for table " + table.getTitle() + " was successful, but response body was empty"));
                                    }

                                    assert body != null;
                                    table.setRemoteId(body.ocs.data.remoteId());
                                    return supplyAsync(() -> {
                                        db.getTableDao().update(table);
                                        return null;
                                    }, db.getSequentialExecutor());

                                } else {
                                    serverErrorHandler.responseToException(response, "Could not push local changes for table " + table.getTitle(), false).ifPresent(this::throwError);
                                    return CompletableFuture.completedFuture(null);
                                }
                            }, workExecutor);
                }).toArray(CompletableFuture[]::new)), workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account) {
        return executeNetworkRequest(account, apis -> apis.apiV2().getTables())
                .thenComposeAsync(response -> {
                    final Collection<Table> fetchedTables;
                    Log.v(TAG, "Pulling remote changes for " + account.getAccountName());

                    switch (response.code()) {
                        case 200 -> {
                            final var responseBody = response.body();
                            if (responseBody == null || responseBody.ocs == null || responseBody.ocs.data == null) {
                                throw new RuntimeException("Response body is null");
                            }

                            fetchedTables = responseBody.ocs.data
                                    .stream()
                                    .map(tableMapper::toEntity)
                                    .peek(table -> {
                                        table.setStatus(DBStatus.VOID);
                                        table.setAccountId(account.getId());
                                        table.setETag(response.headers().get(HEADER_ETAG));
                                    })
                                    .collect(toUnmodifiableSet());
                        }
                        default -> {
                            fetchedTables = Collections.emptySet();
                            serverErrorHandler.responseToException(response, "", true).ifPresent(this::throwError);
                        }
                    }

                    final var tableRemoteIds = fetchedTables.stream().map(AbstractRemoteEntity::getRemoteId).collect(toUnmodifiableSet());
                    return supplyAsync(() -> db.getTableDao().getTableRemoteAndLocalIds(account.getId(), tableRemoteIds), db.getParallelExecutor())
                            .thenAcceptAsync(tableIds -> CompletableFuture.allOf(fetchedTables.stream().map(table -> {
                                final var tableId = tableIds.get(table.getRemoteId());

                                if (tableId == null) {
                                    Log.i(TAG, "← Adding table " + table.getTitle() + " to database");

                                    return supplyAsync(() -> db.getTableDao().insert(table), db.getSequentialExecutor())
                                            .thenAcceptAsync(table::setId, workExecutor);

                                } else {
                                    table.setId(tableId);
                                    Log.i(TAG, "← Updating " + table.getTitle() + " in database");

                                    return supplyAsync(() -> {
                                        db.getTableDao().update(table);
                                        return null;
                                    }, db.getSequentialExecutor())
                                            .thenAcceptAsync(v -> {
                                                if (!table.hasReadPermission()) {
                                                    db.getRowDao().deleteAllFromTable(table.getId());
                                                }
                                            }, db.getSequentialExecutor());
                                }
                            }).toArray(CompletableFuture[]::new)), workExecutor)
                            .thenAcceptAsync(v -> Log.i(TAG, "← Delete all tables except remoteId " + tableRemoteIds), workExecutor)
                            .thenAcceptAsync(v -> db.getTableDao().deleteExcept(account.getId(), tableRemoteIds), db.getSequentialExecutor());
                }, workExecutor);
    }
}
