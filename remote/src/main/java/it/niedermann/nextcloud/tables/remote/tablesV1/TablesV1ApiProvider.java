package it.niedermann.nextcloud.tables.remote.tablesV1;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.tablesV1.adapter.BooleanV1Adapter;
import it.niedermann.nextcloud.tables.remote.tablesV1.adapter.EUserGroupTypeV1Adapter;
import it.niedermann.nextcloud.tables.remote.tablesV1.adapter.InstantV1Adapter;
import it.niedermann.nextcloud.tables.remote.tablesV1.adapter.UserGroupV1ListAdapter;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnRequestV1Dto;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.EUserGroupTypeV1Dto;

@WorkerThread
public class TablesV1ApiProvider<T> extends ApiProvider<T> {

    private static final String API_ENDPOINT_TABLES_V1 = "/index.php/apps/tables/api/1/";


    public TablesV1ApiProvider(@NonNull Context context,
                               @NonNull Account account,
                               @NonNull Class<T> clazz) throws NextcloudFilesAppAccountNotFoundException {
        this(context, account, clazz, new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantV1Adapter(TablesV1API.FORMATTER_PROPERTIES_DATE_TIME))
                .registerTypeAdapter(Boolean.class, new BooleanV1Adapter())
                .registerTypeAdapter(EUserGroupTypeV1Dto.class, new EUserGroupTypeV1Adapter())
                .registerTypeAdapter(TypeTokens.Lists.UserGroupV1Dto, new UserGroupV1ListAdapter()));
    }

    private TablesV1ApiProvider(@NonNull Context context,
                                @NonNull Account account,
                                @NonNull Class<T> clazz,
                                @NonNull GsonBuilder gsonBuilder) throws NextcloudFilesAppAccountNotFoundException {
        super(context, account, clazz, gsonBuilder);
    }

    @Override
    @NonNull
    protected String getEndpoint() {
        return API_ENDPOINT_TABLES_V1;
    }

    private static final class TypeTokens {
        private static final class Lists {
            private static final Type UserGroupV1Dto = new TypeToken<List<ColumnRequestV1Dto.UserGroupV1Dto>>() {}.getType();
        }
    }
}
