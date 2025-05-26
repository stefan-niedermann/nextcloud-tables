package it.niedermann.nextcloud.tables.repository.sync.treesync;

import static java.util.concurrent.CompletableFuture.completedFuture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.RequestHelper;
import it.niedermann.nextcloud.tables.repository.ServerErrorHandler;
import it.niedermann.nextcloud.tables.repository.sync.exception.SyncExceptionWithContext;
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
    @Nullable
    protected final SyncStatusReporter reporter;

    protected AbstractSyncAdapter(@NonNull Context context) {
        this(context, null);
    }

    protected AbstractSyncAdapter(@NonNull Context context,
                                  @Nullable SyncStatusReporter reporter) {
        this.context = context.getApplicationContext();
        this.db = TablesDatabase.getInstance(this.context);
        this.serverErrorHandler = new ServerErrorHandler(this.context);
        this.workExecutor = SharedExecutors.getCPUExecutor();
        this.requestHelper = new RequestHelper(this.context);
        this.reporter = reporter;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushChildChangesWithoutChangedParent(@NonNull Account account) {
        return completedFuture(null);
    }

    protected CompletableFuture<Void> checkRemoteIdNull(@Nullable Long remoteId) {
        if (remoteId != null) {
            final var future = new CompletableFuture<Void>();
            future.completeExceptionally(new IllegalStateException("RemoteId must be null but was " + remoteId));
            return future;
        }

        return completedFuture(null);
    }

    protected CompletableFuture<Long> checkRemoteIdNotNull(@Nullable Long remoteId) {
        if (remoteId == null) {
            final var future = new CompletableFuture<Long>();
            future.completeExceptionally(new IllegalStateException("RemoteId must be not be null."));
            return future;
        }

        return completedFuture(remoteId);
    }

    /**
     * Wraps a checked exception in a {@link CompletionException} and throws it
     *
     * @param throwable checked exception to be thrown within a
     *                  {@link java.util.concurrent.CompletionStage}
     */
    protected void throwError(@NonNull Throwable throwable) {
        throw throwable instanceof CompletionException
                ? (CompletionException) throwable
                : new CompletionException(throwable);
    }

    /// @return unaltered result from previous [CompletionStage]
    /// @throws SyncExceptionWithContext if `exception` is not `null` enriched with the given `attributes`
    @NonNull
    protected <T> BiFunction<T, Throwable, T> provideDebugContext(@Nullable Object... attributes) {
        return (result, exception) -> {
            if (exception != null) {
                final var treeSyncException = new TreeSyncExceptionWithContext(exception);
                if (attributes != null) {
                    for (final var attribute : attributes) {
                        if (attribute instanceof Serializable s) {
                            treeSyncException.provide(s);
                        } else if (attribute != null) {
                            treeSyncException.provide(attribute.toString());
                        }
                    }
                }
                throw treeSyncException;
            }

            return result;
        };
    }
}
