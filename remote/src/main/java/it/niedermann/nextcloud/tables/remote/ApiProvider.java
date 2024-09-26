package it.niedermann.nextcloud.tables.remote;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.GsonBuilder;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.time.Instant;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.SelectionDefault;
import it.niedermann.nextcloud.tables.remote.adapter.InstantAdapter;
import it.niedermann.nextcloud.tables.remote.adapter.SelectionDefaultAdapter;
import it.niedermann.nextcloud.tables.remote.api.OcsAPI;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import it.niedermann.nextcloud.tables.remote.api.TablesV1API;
import retrofit2.NextcloudRetrofitApiBuilder;

@WorkerThread
public class ApiProvider<T> implements AutoCloseable {

    private static final String API_ENDPOINT_OCS = "/ocs/v2.php/cloud/";
    private static final String API_ENDPOINT_TABLES = "/ocs/v2.php/apps/tables/api/2/";
    @Deprecated(forRemoval = true)
    private static final String API_ENDPOINT_TABLES_V1 = "/index.php/apps/tables/api/1/";
    private final NextcloudAPI nextcloudAPI;
    private final T api;

    private ApiProvider(@NonNull Context context,
                        @NonNull Account account,
                        @NonNull Class<T> clazz,
                        @NonNull String endpoint) throws NextcloudFilesAppAccountNotFoundException {
        this.nextcloudAPI = new NextcloudAPI(
                context,
                AccountImporter.getSingleSignOnAccount(context, account.getAccountName()),
                new GsonBuilder()
                        .registerTypeAdapter(Instant.class, new InstantAdapter(TablesAPI.FORMATTER_PROPERTIES_DATE_TIME))
                        .registerTypeAdapter(SelectionDefault.class, new SelectionDefaultAdapter())
                        .create(),
                Throwable::printStackTrace
        );

        this.api = new NextcloudRetrofitApiBuilder(nextcloudAPI, endpoint).create(clazz);
    }

    public static <T> ApiProvider<T> of(@NonNull Context context, @NonNull Account account, @NonNull Class<T> clazz, @NonNull String endpoint) throws NextcloudFilesAppAccountNotFoundException {
        return new ApiProvider<>(context, account, clazz, endpoint);
    }

    public static ApiProvider<OcsAPI> getOcsApiProvider(@NonNull Context context, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        return of(context, account, OcsAPI.class, API_ENDPOINT_OCS);
    }

    public static ApiProvider<TablesAPI> getTablesApiProvider(@NonNull Context context, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        return of(context, account, TablesAPI.class, API_ENDPOINT_TABLES);
    }

    @Deprecated(forRemoval = true)
    public static ApiProvider<TablesV1API> getTablesV1ApiProvider(@NonNull Context context, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        return of(context, account, TablesV1API.class, API_ENDPOINT_TABLES_V1);
    }

    public T getApi() {
        return this.api;
    }

    @Override
    public void close() {
        this.nextcloudAPI.close();
    }
}
