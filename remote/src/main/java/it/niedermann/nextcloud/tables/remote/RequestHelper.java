package it.niedermann.nextcloud.tables.remote;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.ocs.OcsAPI;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.shared.SharedExecutors;
import retrofit2.Call;
import retrofit2.Response;

public class RequestHelper {

    private final Context context;

    public RequestHelper(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /// Convenience method to catch the checked [IOException] when running the
    /// [Call#execute()] method and throw it wrapped in a [CompletionException].
    /// Also takes care about closing resources in a `finally` block.
    @NonNull
    public <TResponse> CompletableFuture<Response<TResponse>> executeOcsRequest(@NonNull Account account,
                                                                                @NonNull Function<OcsAPI, Call<TResponse>> callFactory) {
        return supplyAsync(() -> {
            try (final var apiProvider = ApiProvider.getOcsApiProvider(context, account)) {

                final var api = apiProvider.getApi();
                // TODO Check connectivity
                // TODO Log Request-ID Header in case of a failed request
                return callFactory.apply(api).execute();

            } catch (Exception exception) {
                throw wrapInCompletionExceptionIfNecessary(exception);
            }
        }, getNetworkExecutor(account));
    }

    /// Convenience method to catch the checked [IOException] when running the
    /// [Call#execute()] method and throw it wrapped in a [CompletionException].
    /// Also takes care about closing resources in a `finally` block.
    @Deprecated()
    @NonNull
    public <TResponse> CompletableFuture<Response<TResponse>> executeTablesV1Request(@NonNull Account account,
                                                                                     @NonNull Function<TablesV1API, Call<TResponse>> callFactory) {
        return supplyAsync(() -> {
            try (final var apiProvider = ApiProvider.getTablesV1ApiProvider(context, account)) {

                final var api = apiProvider.getApi();
                // TODO Check connectivity
                // TODO Log Request-ID Header in case of a failed request
                return callFactory.apply(api).execute();

            } catch (Exception exception) {
                throw wrapInCompletionExceptionIfNecessary(exception);
            }
        }, getNetworkExecutor(account));
    }

    /// Convenience method to catch the checked [IOException] when running the
    /// [Call#execute()] method and throw it wrapped in a [CompletionException].
    /// Also takes care about closing resources in a `finally` block.
    @NonNull
    public <TResponse> CompletableFuture<Response<TResponse>> executeTablesV2Request(@NonNull Account account,
                                                                                     @NonNull Function<TablesV2API, Call<TResponse>> callFactory) {
        return supplyAsync(() -> {
            try (final var apiProvider = ApiProvider.getTablesV2ApiProvider(context, account)) {

                final var api = apiProvider.getApi();
                // TODO Check connectivity
                // TODO Log Request-ID Header in case of a failed request
                return callFactory.apply(api).execute();

            } catch (Exception exception) {
                throw wrapInCompletionExceptionIfNecessary(exception);
            }
        }, getNetworkExecutor(account));
    }

    private CompletionException wrapInCompletionExceptionIfNecessary(@Nullable Exception exception) {
        return exception instanceof CompletionException
                ? (CompletionException) exception
                : new CompletionException(exception);
    }

    private ExecutorService getNetworkExecutor(@NonNull Account account) {
        return SharedExecutors.getIONetExecutor(Uri.parse(account.getUrl()));
    }
}
