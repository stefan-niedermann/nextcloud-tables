package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.runAsync;

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
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.tables.shared.SharedExecutors;

public class DefaultSyncAdapter {

    private static final String TAG = DefaultSyncAdapter.class.getSimpleName();
    private static final int PROBABLE_ACCOUNT_COUNT = 1;

    private static final Map<Long, CompletableFuture<Void>> currentSyncs = new HashMap<>(PROBABLE_ACCOUNT_COUNT);
    private static final Map<Long, CompletableFuture<Void>> scheduledSyncs = new HashMap<>(PROBABLE_ACCOUNT_COUNT);

    private final ExecutorService workExecutor;
    private final AbstractSyncAdapter<Account> accountSyncAdapter;

    public DefaultSyncAdapter(@NonNull Context context) {
        this(SharedExecutors.CPU, new AccountSyncAdapter(context));
    }

    private DefaultSyncAdapter(
            @NonNull ExecutorService workExecutor,
            @NonNull AbstractSyncAdapter<Account> accountSyncAdapter
    ) {
        this.workExecutor = workExecutor;
        this.accountSyncAdapter = accountSyncAdapter;
    }

    @AnyThread
    public CompletableFuture<Void> scheduleSynchronization(@NonNull Account account, @Nullable SyncStatusReporter reporter) {

        synchronized (DefaultSyncAdapter.this) {

            final long accountId = account.getId();
            final boolean currentSyncActive = currentSyncs.containsKey(accountId);
            final boolean nextSyncScheduled = scheduledSyncs.containsKey(accountId);
            final boolean noSyncActive = !currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveButNoSyncScheduled = currentSyncActive && !nextSyncScheduled;
            final boolean currentSyncActiveAndAnotherSyncIsScheduled = currentSyncActive && nextSyncScheduled;

            if (noSyncActive) {

                // Currently no sync is active. let's start one!

                Log.i(TAG, "Scheduled (currently none active)");

                currentSyncs.put(accountId, synchronize(account, reporter)
                        .whenCompleteAsync((result, exception) -> {

                            synchronized (DefaultSyncAdapter.this) {

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

                            synchronized (DefaultSyncAdapter.this) {

                                Log.i(TAG, "Scheduled now becomes current one.");
                                currentSyncs.put(accountId, scheduledSyncs.get(accountId));
                                scheduledSyncs.remove(accountId);

                            }

                        }, workExecutor)
                        .thenComposeAsync(v -> synchronize(account, reporter), workExecutor)
                        .thenAcceptAsync(v -> {

                            synchronized (DefaultSyncAdapter.this) {

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

    private CompletableFuture<Void> synchronize(@NonNull Account account, @Nullable SyncStatusReporter reporter) {
        return runAsync(() -> Log.i(TAG, "Start " + account.getAccountName()), workExecutor)
                .thenComposeAsync(v -> accountSyncAdapter.synchronize(account, account, reporter), workExecutor)
                .thenRunAsync(() -> Log.i(TAG, "End " + account.getAccountName()), workExecutor);
    }
}
