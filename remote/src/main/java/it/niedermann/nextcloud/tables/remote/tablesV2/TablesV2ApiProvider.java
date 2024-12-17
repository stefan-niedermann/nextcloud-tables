package it.niedermann.nextcloud.tables.remote.tablesV2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.GsonBuilder;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.time.Instant;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.tablesV2.adapter.ENodeTypeV2Adapter;
import it.niedermann.nextcloud.tables.remote.tablesV2.adapter.EUserGroupV2Adapter;
import it.niedermann.nextcloud.tables.remote.tablesV2.adapter.InstantV2Adapter;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.EUserGroupTypeV2Dto;

@WorkerThread
public class TablesV2ApiProvider<T> extends ApiProvider<T> {

    private static final String API_ENDPOINT_TABLES_V2 = "/ocs/v2.php/apps/tables/api/2/";

    public TablesV2ApiProvider(@NonNull Context context,
                               @NonNull Account account,
                               @NonNull Class<T> clazz) throws NextcloudFilesAppAccountNotFoundException {
        this(context, account, clazz, new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantV2Adapter(TablesV2API.FORMATTER_PROPERTIES_DATE_TIME))
                .registerTypeAdapter(EUserGroupTypeV2Dto.class, new EUserGroupV2Adapter())
                .registerTypeAdapter(ENodeTypeV2Dto.class, new ENodeTypeV2Adapter()));
    }

    private TablesV2ApiProvider(@NonNull Context context,
                                @NonNull Account account,
                                @NonNull Class<T> clazz,
                                @NonNull GsonBuilder gsonBuilder) throws NextcloudFilesAppAccountNotFoundException {
        super(context, account, clazz, gsonBuilder);
    }

    @Override
    @NonNull
    protected String getEndpoint() {
        return API_ENDPOINT_TABLES_V2;
    }
}
