package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import it.niedermann.nextcloud.tables.database.entity.Account;

public class SyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();
    private static final int PROBABLE_ACCOUNT_COUNT = 1;

    private static final Map<Long, CompletableFuture<Void>> currentSyncs = new HashMap<>(PROBABLE_ACCOUNT_COUNT);
    private static final Map<Long, CompletableFuture<Void>> scheduledSyncs = new HashMap<>(PROBABLE_ACCOUNT_COUNT);

    private final ExecutorService workExecutor;
    private final AbstractSyncAdapter capabilitiesSyncAdapter;
    private final AbstractSyncAdapter userSyncAdapter;
    private final AbstractSyncAdapter tableSyncAdapter;
    private final AbstractSyncAdapter columnSyncAdapter;
    private final AbstractSyncAdapter rowSyncAdapter;

    public SyncAdapter(@NonNull Context context) {
        this(ForkJoinPool.commonPool(),
                new CapabilitiesSyncAdapter(context),
                new UserSyncAdapter(context),
                new TableSyncAdapter(context),
                new ColumnSyncAdapter(context),
                new RowSyncAdapter(context));
    }

    private SyncAdapter(
            @NonNull ExecutorService workExecutor,
            @NonNull AbstractSyncAdapter capabilitiesSyncAdapter,
            @NonNull AbstractSyncAdapter userSyncAdapter,
            @NonNull AbstractSyncAdapter tableSyncAdapter,
            @NonNull AbstractSyncAdapter columnSyncAdapter,
            @NonNull AbstractSyncAdapter rowSyncAdapter
    ) {
        this.workExecutor = workExecutor;
        this.capabilitiesSyncAdapter = capabilitiesSyncAdapter;
        this.userSyncAdapter = userSyncAdapter;
        this.tableSyncAdapter = tableSyncAdapter;
        this.columnSyncAdapter = columnSyncAdapter;
        this.rowSyncAdapter = rowSyncAdapter;
    }

    @AnyThread
    public CompletableFuture<Void> scheduleSynchronization(@NonNull Account account) {

        synchronized (SyncAdapter.this) {

            final long accountId = account.getId();
            final boolean currentSyncActive = currentSyncs.containsKey(accountId);
            final boolean nextSyncScheduled = scheduledSyncs.containsKey(accountId);
            final boolean noSyncActive = !currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveButNoSyncScheduled = currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveAndAnotherSyncIsScheduled = currentSyncActive && nextSyncScheduled;

            if (noSyncActive) {

                // Currently no sync is active. let's start one!

                Log.i(TAG, "Scheduled (currently none active)");

                currentSyncs.put(accountId, synchronize(account)
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (SyncAdapter.this) {

                                Log.i(TAG, "Current sync finished.");
                                currentSyncs.remove(accountId);

                            }

                        }, workExecutor));

                return currentSyncs.get(accountId);

            } else if (currentSyncActiveButNoSyncScheduled) {

                // There is a sync in progress, but no scheduled sync.
                // Let's schedule a sync that waits for the current sync being done and then
                // switches the scheduledSync to the current

                Log.i(TAG, "Scheduled to the end of the current one.");

                scheduledSyncs.put(accountId, requireNonNull(currentSyncs.get(accountId))
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (SyncAdapter.this) {

                                Log.i(TAG, "Scheduled now becomes current one.");
                                currentSyncs.put(accountId, scheduledSyncs.get(accountId));
                                scheduledSyncs.remove(accountId);

                            }

                        }, workExecutor)
                        .thenApplyAsync(v -> account, workExecutor)
                        .thenComposeAsync(this::synchronize, workExecutor)
                        .thenAcceptAsync(v -> {

                            synchronized (SyncAdapter.this) {

                                Log.i(TAG, "Current sync finished.");
                                currentSyncs.remove(accountId);

                            }

                        }, workExecutor));

                return scheduledSyncs.get(accountId);

            } else if (currentSyncActiveAndAnotherSyncIsScheduled) {

                // There is a sync in progress and a scheduled one. It is safe to simply return the scheduled one.

                Log.i(TAG, "Returned scheduled one");

                return scheduledSyncs.get(accountId);

            }

        }

        // It should not be possible to have a scheduled sync but no actively running one

        final var future = new CompletableFuture<Void>();
        future.completeExceptionally(new IllegalStateException("currentSync is null but scheduledSync is not null."));
        return future;

    }


    private CompletableFuture<Void> synchronize(@NonNull Account account) {
        return runAsync(() -> Log.i(TAG, "Start " + account.getAccountName()), workExecutor)
                .thenComposeAsync(v ->

                        // If NextcloudVersion or TablesVersion is null,
                        // the first request must pull Capabilities to determine
                        // if we support the tables server version and
                        // whether the server is in maintenance mode.
                        // We therefore skip the push of local changes.
                        // The only valid scenario where this happens is when importing an account.

                        account.getNextcloudVersion() != null && account.getTablesVersion() != null
                                ? this.pushLocalChanges(account)
                                : completedFuture(account), workExecutor)

                .thenComposeAsync(this::pullRemoteChanges, workExecutor)
                .thenRunAsync(() -> Log.i(TAG, "End " + account.getAccountName()), workExecutor);
    }

    @NonNull
    private CompletableFuture<Account> pushLocalChanges(@NonNull Account account) {
        return completedFuture(account)
                .thenComposeAsync(capabilitiesSyncAdapter::pushLocalChanges, workExecutor)
                .thenComposeAsync(userSyncAdapter::pushLocalChanges, workExecutor)
                .thenComposeAsync(tableSyncAdapter::pushLocalChanges, workExecutor)
                .thenComposeAsync(columnSyncAdapter::pushLocalChanges, workExecutor)
                .thenComposeAsync(rowSyncAdapter::pushLocalChanges, workExecutor);
    }

    @NonNull
    private CompletableFuture<Account> pullRemoteChanges(@NonNull Account account) {
        return completedFuture(account)
                .thenComposeAsync(capabilitiesSyncAdapter::pullRemoteChanges, workExecutor)
                .thenComposeAsync(userSyncAdapter::pullRemoteChanges, workExecutor)
                .thenComposeAsync(tableSyncAdapter::pullRemoteChanges, workExecutor)
                .thenComposeAsync(columnSyncAdapter::pullRemoteChanges, workExecutor)
                .thenComposeAsync(rowSyncAdapter::pullRemoteChanges, workExecutor);
    }
}