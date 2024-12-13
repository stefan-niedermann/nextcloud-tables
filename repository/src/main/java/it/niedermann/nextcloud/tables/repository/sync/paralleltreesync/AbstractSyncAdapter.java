package it.niedermann.nextcloud.tables.repository.sync.paralleltreesync;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.model.ocs.OcsResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractEntity;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.ocs.OcsAPI;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.repository.ServerErrorHandler;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.tables.shared.SharedExecutors;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

abstract class AbstractSyncAdapter<TParentEntity extends AbstractEntity> implements SyncAdapter<TParentEntity> {

    private static final String TAG = AbstractSyncAdapter.class.getSimpleName();
    protected static final String HEADER_ETAG = "ETag";
    protected final Context context;
    protected final TablesDatabase db;
    protected final ServerErrorHandler serverErrorHandler;
    protected final ExecutorService workExecutor;
    private final ExecutorService networkExecutor;

    protected AbstractSyncAdapter(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.db = TablesDatabase.getInstance(this.context);
        this.serverErrorHandler = new ServerErrorHandler(this.context);
        this.workExecutor = SharedExecutors.CPU;
        this.networkExecutor = SharedExecutors.IO_NET;
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

    /**
     * Convenience method to catch the checked {@link IOException} when running the
     * {@link Call#execute()} method and throw it wrapped in a {@link CompletionException}.
     * Also takes care about closing resources in a <code>finally</code> block.
     */
    @NonNull
    protected <TResponse> CompletableFuture<Response<TResponse>> executeNetworkRequest(@NonNull Account account,
                                                                                       @NonNull Function<ApiProvider.ApiTuple, Call<TResponse>> api) {
        return supplyAsync(() -> {
            final var ocsProviderRef = new AtomicReference<ApiProvider<OcsAPI>>();
            final var apiV2ProviderRef = new AtomicReference<ApiProvider<TablesV2API>>();
            final var apiV1ProviderRef = new AtomicReference<ApiProvider<TablesV1API>>();

            try {
                ocsProviderRef.set(ApiProvider.getOcsApiProvider(context, account));
                apiV2ProviderRef.set(ApiProvider.getTablesV2ApiProvider(context, account));
                apiV1ProviderRef.set(ApiProvider.getTablesV1ApiProvider(context, account));

                final var apiTuple = new ApiProvider.ApiTuple(
                        ocsProviderRef.get().getApi(),
                        apiV2ProviderRef.get().getApi(),
                        apiV1ProviderRef.get().getApi());

                // TODO Check connectivity
                return api.apply(apiTuple).execute();

            } catch (Exception throwable) {
                throw throwable instanceof CompletionException
                        ? (CompletionException) throwable
                        : new CompletionException(throwable);

            } finally {
                Stream.of(ocsProviderRef, apiV2ProviderRef, apiV1ProviderRef)
                        .map(AtomicReference::get)
                        .forEach(ApiProvider::close);
            }
        }, networkExecutor);
    }

    /// V1 API does not wrap in OcsResponse objects
    protected <T> Response<OcsResponse<T>> wrapInOcsResponse(@NonNull Response<T> result) {
        final var response = new OcsResponse<T>();
        response.ocs = new OcsResponse.OcsWrapper<>();
        response.ocs.data = result.body();
        response.ocs.meta = new OcsResponse.OcsWrapper.OcsMeta();
        response.ocs.meta.statusCode = result.code();
        response.ocs.meta.message = result.message();

        return result.isSuccessful()
                ? Response.success(response)
                : Response.error(Optional.ofNullable(result.errorBody())
                        .orElse(ResponseBody.create("", MediaType.get(""))),
                result.raw());
    }

    /**
     * @return {@link Table#getRemoteId()} of {@param table} or a failed future.
     * @noinspection SameParameterValue
     */
    @NonNull
    protected CompletableFuture<Long> getRemoteIdOrThrow(@NonNull AbstractRemoteEntity entity,
                                                         @NonNull Class<? extends AbstractRemoteEntity> entityType) {
        return supplyAsync(() -> {
            final var tableRemoteId = entity.getRemoteId();

            if (tableRemoteId == null) {
                throw new IllegalStateException("Expected " + Table.class.getSimpleName() + " remote ID to be present when pushing " + entityType.getSimpleName() + " changes, but was null");
            }

            return tableRemoteId;
        }, workExecutor);
    }

    protected CompletableFuture<Void> checkRemoteIdNull(@Nullable Long remoteId) {
        if (remoteId != null) {
            final var future = new CompletableFuture<Void>();
            future.completeExceptionally(new IllegalStateException("RemoteID must be null but was " + remoteId));
            return future;
        }

        return completedFuture(null);
    }

    protected CompletableFuture<Void> checkRemoteIdNotNull(@Nullable Long remoteId) {
        if (remoteId == null) {
            final var future = new CompletableFuture<Void>();
            future.completeExceptionally(new IllegalStateException("RemoteID must be not be null."));
            return future;
        }

        return completedFuture(null);
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
