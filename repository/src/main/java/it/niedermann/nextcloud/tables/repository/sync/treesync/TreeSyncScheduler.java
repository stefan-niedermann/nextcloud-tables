package it.niedermann.nextcloud.tables.repository.sync.treesync;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static it.niedermann.nextcloud.tables.shared.Constants.PROBABLE_ACCOUNT_COUNT;

import android.content.Context;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.sync.SyncScheduler;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.tables.shared.SharedExecutors;

/// Synchronization is executed parallel in general but sequentially per [Account].
public class TreeSyncScheduler implements SyncScheduler {

    private static final String TAG = TreeSyncScheduler.class.getSimpleName();

    private static final Map<Long, CompletableFuture<Void>> currentSyncs = new HashMap<>(PROBABLE_ACCOUNT_COUNT);
    private static final Map<Long, CompletableFuture<Void>> scheduledSyncs = new HashMap<>(PROBABLE_ACCOUNT_COUNT);

    private final Context context;
    private final ExecutorService workExecutor;

    public TreeSyncScheduler(@NonNull Context context) {
        this(context, SharedExecutors.getCPUExecutor());
    }

    private TreeSyncScheduler(@NonNull Context context,
                              @NonNull ExecutorService workExecutor) {
        this.context = context.getApplicationContext();
        this.workExecutor = workExecutor;
    }

    @AnyThread
    public CompletableFuture<Void> scheduleSynchronization(@NonNull Account account,
                                                           @Nullable SyncStatusReporter reporter) {

        synchronized (TreeSyncScheduler.this) {

            final long accountId = account.getId();
            final boolean currentSyncActive = currentSyncs.containsKey(accountId);
            final boolean nextSyncScheduled = scheduledSyncs.containsKey(accountId);
            final boolean noSyncActive = !currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveButNoSyncScheduled = currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveAndAnotherSyncIsScheduled = currentSyncActive && nextSyncScheduled;

            if (noSyncActive) {

                // Currently no sync is active. let's start one!

                Log.i(TAG, "Scheduled (currently none active)");

                currentSyncs.put(accountId, synchronize(this.context, account, reporter)
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (TreeSyncScheduler.this) {

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

                            synchronized (TreeSyncScheduler.this) {

                                Log.i(TAG, "Scheduled now becomes current one.");
                                currentSyncs.put(accountId, scheduledSyncs.get(accountId));
                                scheduledSyncs.remove(accountId);

                            }

                        }, workExecutor)
                        .thenComposeAsync(v -> synchronize(this.context, account, reporter), workExecutor)
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (TreeSyncScheduler.this) {

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

    /// **Guaranteed executing orders**
    ///
    /// *Step 1 - 4* (may be omitted in case [Account#getTablesVersion] or [Account#getNextcloudVersion] is `null`)
    /// 1. [SyncAdapter#pushLocalDeletions]
    /// 2. [SyncAdapter#pushLocalCreations]
    /// 3. [SyncAdapter#pushLocalUpdates]
    /// 4. [SyncAdapter#pushChildChangesWithoutChangedParent]
    ///
    /// *Step 5* (runs always at the end)
    /// 5. [SyncAdapter#pullRemoteChanges]
    private CompletableFuture<Void> synchronize(@NonNull Context context,
                                                @NonNull Account account,
                                                @Nullable SyncStatusReporter reporter) {
        return runAsync(() -> Log.i(TAG, "Start " + account.getAccountName()), workExecutor)

                .thenApplyAsync(v -> new AccountSyncAdapter(context, reporter), workExecutor)
                .thenComposeAsync(accountSyncAdapter -> completedFuture(null)

                        .thenComposeAsync(v -> pushLocalChanges(accountSyncAdapter, account), workExecutor)
                        .thenComposeAsync(v -> pullRemoteChanges(accountSyncAdapter, account), workExecutor))

                .whenCompleteAsync((result, exception) -> Log.i(TAG, "End " + account.getAccountName()), workExecutor);
    }

    private CompletableFuture<Void> pushLocalChanges(@NonNull SyncAdapter<Account> syncAdapter,
                                                     @NonNull Account account) {
        return completedFuture(account.getTablesVersion() != null && account.getNextcloudVersion() != null)
                .thenComposeAsync(pushPossible -> {

                    if (pushPossible) {

                        return completedFuture(null)
                                .thenComposeAsync(v -> syncAdapter.pushLocalDeletions(account, account), workExecutor)
                                .thenComposeAsync(v -> syncAdapter.pushLocalCreations(account, account), workExecutor)
                                .thenComposeAsync(v -> syncAdapter.pushLocalUpdates(account, account), workExecutor)
                                .thenComposeAsync(v -> syncAdapter.pushChildChangesWithoutChangedParent(account), workExecutor);

                    } else {

                        // No  information about the server or the tables server app  is available, so we can't safely push local changes.
                        // This is expected when synchronizing an Account the very first time.
                        // We recover by simply skipping the push part and proceed with pulling remote changes.

                        return completedFuture(null);
                    }

                });
    }

    private CompletableFuture<Void> pullRemoteChanges(@NonNull SyncAdapter<Account> syncAdapter,
                                                      @NonNull Account account) {
        return syncAdapter.pullRemoteChanges(account, account);
    }
}