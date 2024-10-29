package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import it.niedermann.nextcloud.tables.database.entity.Account;

public class SyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();

    private final ExecutorService executor;
    private final AbstractSyncAdapter capabilitiesSyncAdapter;
    private final AbstractSyncAdapter userSyncAdapter;
    private final AbstractSyncAdapter tableSyncAdapter;
    private final AbstractSyncAdapter columnSyncAdapter;
    private final AbstractSyncAdapter rowSyncAdapter;

    @Nullable
    private static CompletableFuture<Void> currentSync;
    @Nullable
    private static CompletableFuture<Void> scheduledSync;


    public SyncAdapter(@NonNull Context context) {
        this(ForkJoinPool.commonPool(),
                new CapabilitiesSyncAdapter(context),
                new UserSyncAdapter(context),
                new TableSyncAdapter(context),
                new ColumnSyncAdapter(context),
                new RowSyncAdapter(context));
    }

    private SyncAdapter(
            @NonNull ExecutorService executor,
            @NonNull AbstractSyncAdapter capabilitiesSyncAdapter,
            @NonNull AbstractSyncAdapter userSyncAdapter,
            @NonNull AbstractSyncAdapter tableSyncAdapter,
            @NonNull AbstractSyncAdapter columnSyncAdapter,
            @NonNull AbstractSyncAdapter rowSyncAdapter
    ) {
        this.executor = executor;
        this.capabilitiesSyncAdapter = capabilitiesSyncAdapter;
        this.userSyncAdapter = userSyncAdapter;
        this.tableSyncAdapter = tableSyncAdapter;
        this.columnSyncAdapter = columnSyncAdapter;
        this.rowSyncAdapter = rowSyncAdapter;
    }

    @AnyThread
    public synchronized CompletableFuture<Void> scheduleSynchronization(@NonNull Account account) {
        synchronized (SyncAdapter.this) {
            if (currentSync == null && scheduledSync == null) {

                // Currently no sync is active. let's start one!

                currentSync = synchronize(account);

                return currentSync;

            } else if (scheduledSync == null) {

                // There is a sync in progress, but no other synced.
                // Let's schedule a sync that waits for the current sync being done and then switches the scheduledSync to the current

                scheduledSync = currentSync
                        .thenRunAsync(() -> {
                            synchronized (SyncAdapter.this) {
                                currentSync = scheduledSync;
                                scheduledSync = null;
                            }
                        }, executor)
                        .thenComposeAsync(v -> synchronize(account), executor)
                        .thenRunAsync(() -> pullRemoteChanges(account), executor)
                        .whenCompleteAsync((v, throwable) -> currentSync = null, executor);

                return scheduledSync;

            } else if (currentSync == null) {

                // It should not be possible to have a scheduled sync but no actively running one

                final var future = new CompletableFuture<Void>();
                future.completeExceptionally(new IllegalStateException("currentSync is null but scheduledSync is not null."));
                return future;

            } else {

                // There is a sync in progress and a scheduled one. It is safe to simply return the scheduled one.

                return scheduledSync;
            }
        }
    }


    @AnyThread
    private CompletableFuture<Void> synchronize(@NonNull Account account) {
        return supplyAsync(() -> Log.i(TAG, "[Synchronization] Start " + account.getAccountName()), executor)
                .thenComposeAsync(v ->

                        // If NextcloudVersion or TablesVersion is null,
                        // the first request must pull Capabilities to determine
                        // if we support the tables server version and
                        // whether the server is in maintenance mode.
                        // We therefore skip the push of local changes.
                        // The only valid scenario where this happens is when importing an account.

                        account.getNextcloudVersion() != null && account.getTablesVersion() != null
                                ? pushLocalChanges(account)
                                : CompletableFuture.completedFuture(null), executor)

                .thenRunAsync(() -> pullRemoteChanges(account), executor)
                .thenRunAsync(() -> Log.i(TAG, "[Synchronization] End " + account.getAccountName()), executor);
    }

    @NonNull
    private CompletableFuture<Void> pushLocalChanges(@NonNull Account account) {
        return runAsync(() -> capabilitiesSyncAdapter.pushLocalChanges(account), executor)
                .thenRunAsync(() -> userSyncAdapter.pushLocalChanges(account), executor)
                .thenRunAsync(() -> tableSyncAdapter.pushLocalChanges(account), executor)
                .thenRunAsync(() -> columnSyncAdapter.pushLocalChanges(account), executor)
                .thenRunAsync(() -> rowSyncAdapter.pushLocalChanges(account), executor);
    }

    @NonNull
    private CompletableFuture<Void> pullRemoteChanges(@NonNull Account account) {
        return runAsync(() -> capabilitiesSyncAdapter.pullRemoteChanges(account))
                .thenRunAsync(() -> userSyncAdapter.pullRemoteChanges(account))
                .thenRunAsync(() -> tableSyncAdapter.pullRemoteChanges(account))
                .thenRunAsync(() -> columnSyncAdapter.pullRemoteChanges(account))
                .thenRunAsync(() -> rowSyncAdapter.pullRemoteChanges(account));
    }
}
