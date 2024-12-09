package it.niedermann.nextcloud.tables.remote;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.GsonBuilder;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.ocs.OcsAPI;
import it.niedermann.nextcloud.tables.remote.ocs.OcsApiProvider;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1ApiProvider;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2ApiProvider;
import retrofit2.NextcloudRetrofitApiBuilder;

@WorkerThread
public abstract class ApiProvider<T> implements AutoCloseable {

    protected final NextcloudAPI nextcloudAPI;
    protected final T api;

    protected ApiProvider(@NonNull Context context,
                          @NonNull Account account,
                          @NonNull Class<T> clazz,
                          @NonNull GsonBuilder gsonBuilder) throws NextcloudFilesAppAccountNotFoundException {
        this.nextcloudAPI = new NextcloudAPI(
                context,
                AccountImporter.getSingleSignOnAccount(context, account.getAccountName()),
                gsonBuilder.create(),
                Throwable::printStackTrace);

        this.api = new NextcloudRetrofitApiBuilder(nextcloudAPI, getEndpoint()).create(clazz);
    }

    @NonNull
    protected abstract String getEndpoint();

    public static ApiProvider<OcsAPI> getOcsApiProvider(@NonNull Context context,
                                                        @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        return new OcsApiProvider<>(context, account, OcsAPI.class);
    }

    @Deprecated()
    public static ApiProvider<TablesV1API> getTablesV1ApiProvider(@NonNull Context context,
                                                                  @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        return new TablesV1ApiProvider<>(context, account, TablesV1API.class);
    }

    public static ApiProvider<TablesV2API> getTablesV2ApiProvider(@NonNull Context context,
                                                                  @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        return new TablesV2ApiProvider<>(context, account, TablesV2API.class);
    }

    public T getApi() {
        return this.api;
    }

    @Override
    public void close() {
        this.nextcloudAPI.close();
    }

    public record ApiTuple(
            @NonNull OcsAPI ocs,
            @NonNull TablesV2API apiV2,
            @NonNull TablesV1API apiV1
    ) {

    }
}
