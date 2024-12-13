package it.niedermann.nextcloud.tables.repository.sync.paralleltreesync;

import static java.util.concurrent.CompletableFuture.completedFuture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.RequestHelper;
import it.niedermann.nextcloud.tables.repository.ServerErrorHandler;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.tables.shared.SharedExecutors;

abstract class AbstractSyncAdapter<TParentEntity extends AbstractEntity> implements SyncAdapter<TParentEntity> {

    private static final String TAG = AbstractSyncAdapter.class.getSimpleName();
    protected static final String HEADER_ETAG = "ETag";
    protected final Context context;
    protected final TablesDatabase db;
    protected final ServerErrorHandler serverErrorHandler;
    protected final ExecutorService workExecutor;
    protected final RequestHelper requestHelper;

    protected AbstractSyncAdapter(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.db = TablesDatabase.getInstance(this.context);
        this.serverErrorHandler = new ServerErrorHandler(this.context);
        this.workExecutor = SharedExecutors.CPU;
        this.requestHelper = new RequestHelper(this.context);
    }

    /// **Guaranteed orders**
    ///
    /// *Step 1 - 4* (may be omitted in case [Account#getTablesVersion] or [Account#getNextcloudVersion] is `null`)
    /// 1. [#pushLocalDeletions]
    /// 2. [#pushLocalCreations]
    /// 3. [#pushLocalUpdates]
    /// 4. [#pushChildChangesWithoutChangedParent]
    ///
    /// *Step 5* (runs always at the end)
    /// 5. [#pullRemoteChanges]
    @NonNull
    @Override
    public final CompletableFuture<Void> synchronize(@NonNull Account account,
                                                     @NonNull TParentEntity parentEntity,
                                                     @Nullable SyncStatusReporter reporter) {
        final var pushLocalChanges = account.getTablesVersion() == null || account.getNextcloudVersion() == null
                ? completedFuture(null)
                : completedFuture(null)
                .thenComposeAsync(v -> pushLocalDeletions(account, parentEntity), workExecutor)
                .thenComposeAsync(v -> pushLocalCreations(account, parentEntity), workExecutor)
                .thenComposeAsync(v -> pushLocalUpdates(account, parentEntity), workExecutor)
                .thenComposeAsync(v -> pushChildChangesWithoutChangedParent(account), workExecutor);

        return pushLocalChanges
                .thenComposeAsync(v -> pullRemoteChanges(account, parentEntity, reporter), workExecutor);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushChildChangesWithoutChangedParent(@NonNull Account account) {
        return completedFuture(null);
    }

    protected CompletableFuture<Void> checkRemoteIdNull(@Nullable Long remoteId) {
        if (remoteId != null) {
            final var future = new CompletableFuture<Void>();
            future.completeExceptionally(new IllegalStateException("RemoteID must be null but was " + remoteId));
            return future;
        }

        return completedFuture(null);
    }

    protected CompletableFuture<Long> checkRemoteIdNotNull(@Nullable Long remoteId) {
        if (remoteId == null) {
            final var future = new CompletableFuture<Long>();
            future.completeExceptionally(new IllegalStateException("RemoteID must be not be null."));
            return future;
        }

        return completedFuture(remoteId);
    }

    /**
     * Wraps a checked exception in a {@link CompletionException} and throws it
     * @param throwable checked exception to be thrown within a
     * {@link java.util.concurrent.CompletionStage}
     */
    protected void throwError(@NonNull Throwable throwable) {
        throw throwable instanceof CompletionException
                ? (CompletionException) throwable
                : new CompletionException(throwable);
    }
}
