package it.niedermann.nextcloud.tables.remote;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

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

    /**
     * Convenience method to catch the checked {@link IOException} when running the
     * {@link Call#execute()} method and throw it wrapped in a {@link CompletionException}.
     * Also takes care about closing resources in a <code>finally</code> block.
     */
    @NonNull
    public <TResponse> CompletableFuture<Response<TResponse>> executeNetworkRequest(@NonNull Account account,
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
        }, getNetworkExecutor(account));
    }

    private ExecutorService getNetworkExecutor(@NonNull Account account) {
        return SharedExecutors.getIONetExecutor(Uri.parse(account.getUrl()));
    }
}
