package it.niedermann.nextcloud.tables.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncWorker extends Worker {

    private static final Logger logger = Logger.getLogger(SyncWorker.class.getSimpleName());

    private static final String WORKER_TAG = "it.niedermann.nextcloud.tables.background_synchronization";

    private final AccountRepository accountRepository;
    private final PreferencesRepository preferencesRepository;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        accountRepository = new AccountRepository(context);
        preferencesRepository = new PreferencesRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        logger.info("Starting background synchronization");
        preferencesRepository.setLastBackgroundSync(Instant.now());

        try {
            final var accounts = accountRepository.getAccounts();

            if (accounts.isEmpty()) {
                return Result.success();
            }

            final var success = new AtomicReference<>(Result.success());

            CompletableFuture.allOf(accounts.stream().map(account -> accountRepository.scheduleSynchronization(account)
                    .whenCompleteAsync((result, exception) -> {
                        if (exception != null) {
                            logger.log(Level.SEVERE, exception.toString(), exception);
                            success.set(Result.failure());
                        }

                    })).toArray(CompletableFuture[]::new)).get();

            return success.get();

        } catch (InterruptedException | ExecutionException exception) {
            logger.log(Level.SEVERE, exception.toString(), exception);
            return Result.failure();

        } finally {
            logger.info("Finishing background synchronization.");
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
        logger.info(() -> "Deregistering all " + SyncWorker.class.getSimpleName() + " with tag " + WORKER_TAG);
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

        logger.info(() -> "Registering " + SyncWorker.class.getSimpleName() + " running each " + repeatInterval + " " + repeatIntervalTimeUnit);

        WorkManager
                .getInstance(context.getApplicationContext())
                .enqueueUniquePeriodicWork(SyncWorker.WORKER_TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, periodicWorkRequest);
    }
}
