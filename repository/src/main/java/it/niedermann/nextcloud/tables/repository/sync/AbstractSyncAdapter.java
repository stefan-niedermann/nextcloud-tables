package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.ocs.OcsAPI;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.repository.ServerErrorHandler;
import retrofit2.Call;
import retrofit2.Response;

public abstract class AbstractSyncAdapter {

    private static final ExecutorService NETWORK_EXECUTOR = Executors.newCachedThreadPool();
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
        this.workExecutor = ForkJoinPool.commonPool();
        this.networkExecutor = NETWORK_EXECUTOR;
    }

    @NonNull
    public abstract CompletableFuture<Account> pushLocalChanges(@NonNull Account account);

    @NonNull
    public abstract CompletableFuture<Account> pullRemoteChanges(@NonNull Account account);

    /**
     * Convenience method to catch the checked {@link IOException} when running the
     * {@link Call#execute()} method and throw it wrapped in a {@link CompletionException}.
     * Also takes care about closing resources in a <code>finally</code> block.
     */
    @NonNull
    protected <T> CompletableFuture<Response<T>> executeNetworkRequest(@NonNull Account account,
                                                                       @NonNull Function<ApiProvider.ApiTuple, Call<T>> api) {
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

    /**
     * @return {@link Table#getRemoteId()} of {@param table} or a failed future.
     */
    @NonNull
    protected CompletableFuture<Long> getTableRemoteIdOrThrow(@NonNull Table table,
                                                              @NonNull Class<? extends AbstractRemoteEntity> entityType) {
        return supplyAsync(() -> {
            final var tableRemoteId = table.getRemoteId();

            if (tableRemoteId == null) {
                throw new IllegalStateException("Expected " + Table.class.getSimpleName() + " remote ID to be present when pushing " + entityType.getSimpleName() + " changes, but was null");
            }

            return tableRemoteId;
        }, workExecutor);
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
