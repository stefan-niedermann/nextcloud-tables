package it.niedermann.nextcloud.tables.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.sync.SyncScheduler;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.tables.shared.SharedExecutors;

public abstract class AbstractRepository {

    protected final Context context;
    protected final TablesDatabase db;
    protected final ExecutorService workExecutor;
    private final SyncScheduler treeSyncAdapter;

    protected AbstractRepository(@NonNull Context context) {
        this(context, new SyncScheduler.Factory(context));
    }

    private AbstractRepository(@NonNull Context context,
                               @NonNull SyncScheduler.Factory syncSchedulerFactory) {
        this.context = context.getApplicationContext();
        this.db = TablesDatabase.getInstance(this.context);
        this.treeSyncAdapter = syncSchedulerFactory.create();
        this.workExecutor = SharedExecutors.getCPUExecutor();
    }

    /// @noinspection UnusedReturnValue
    protected CompletableFuture<Void> schedulePush(@NonNull Account account) {
        return this.schedulePush(account, null);
    }

    /// @noinspection SameParameterValue
    protected CompletableFuture<Void> schedulePush(@NonNull Account account,
                                                   @Nullable SyncStatusReporter reporter) {
        return this.treeSyncAdapter.scheduleSynchronization(account, SyncScheduler.Scope.PUSH_ONLY, reporter);
    }

    protected CompletableFuture<Void> scheduleSynchronization(@NonNull Account account) {
        return this.scheduleSynchronization(account, null);
    }

    protected CompletableFuture<Void> scheduleSynchronization(@NonNull Account account,
                                                              @Nullable SyncStatusReporter reporter) {
        return this.treeSyncAdapter.scheduleSynchronization(account, SyncScheduler.Scope.PUSH_AND_PULL, reporter);
    }
}
