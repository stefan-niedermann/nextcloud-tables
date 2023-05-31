package it.niedermann.nextcloud.tables.remote;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.PreferencesRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class SyncWorker extends Worker {

    private static final String TAG = SyncWorker.class.getSimpleName();
    private static final String WORKER_TAG = "it.niedermann.nextcloud.tables.background_synchronization";

    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;
    private final PreferencesRepository preferencesRepository;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        accountRepository = new AccountRepository(context);
        tablesRepository = new TablesRepository(context);
        preferencesRepository = new PreferencesRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Starting background synchronization");
        preferencesRepository.setLastBackgroundSync(Instant.now());

        try {
            final var accounts = accountRepository.getAccounts();

            if (accounts.isEmpty()) {
                return Result.success();
            }

            final var success = new AtomicReference<>(Result.success());
            final var latch = new CountDownLatch(accounts.size());

            for (final var account : accounts) {
                try {
                    accountRepository.synchronizeAccount(account);
                    tablesRepository.synchronizeTables(account);
                    latch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                    success.set(Result.failure());
                }
            }

            return success.get();
        } finally {
            Log.i(TAG, "Finishing background synchronization.");
            preferencesRepository.setLastBackgroundSync(Instant.now());
        }
    }

    /**
     * Removes existing {@link SyncWorker} instances and, if background sync is enabled according to the user preferences, it will add a {@link SyncWorker} instance again.
     */
    public static void update(@NonNull Context context) {
        final var preferencesRepository = new PreferencesRepository(context);
        update(context, preferencesRepository.isBackgroundSyncEnabled());
    }

    /**
     * Removes existing {@link SyncWorker} instances and, if background sync is enabled according to the given preferenceValue, it will add a {@link SyncWorker} instance again.
     */
    public static void update(@NonNull Context context, boolean preferenceValue) {
        Log.i(TAG, "Deregistering all " + SyncWorker.class.getSimpleName() + " with tag " + WORKER_TAG);
        WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag(WORKER_TAG);

        final var preferencesRepository = new PreferencesRepository(context);

        if (!preferenceValue) {
            preferencesRepository.setLastBackgroundSync(null);
            return;
        }

        final var repeatInterval = 15L;
        final var repeatIntervalTimeUnit = TimeUnit.MINUTES;
        final var networkType = preferencesRepository.syncOnlyOnWifi() ? NetworkType.UNMETERED : NetworkType.CONNECTED;

        final var constraints = new Constraints
                .Builder()
                .setRequiredNetworkType(networkType)
                .build();

        final var periodicWorkRequest = new PeriodicWorkRequest
                .Builder(SyncWorker.class, repeatInterval, repeatIntervalTimeUnit)
                .setConstraints(constraints)
                .build();

        Log.i(TAG, "Registering " + SyncWorker.class.getSimpleName() + " running each " + repeatInterval + " " + repeatIntervalTimeUnit);

        WorkManager
                .getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(SyncWorker.WORKER_TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, periodicWorkRequest);
    }
}
