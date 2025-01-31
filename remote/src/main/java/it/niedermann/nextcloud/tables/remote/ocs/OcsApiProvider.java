package it.niedermann.nextcloud.tables.remote.ocs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.ApiProvider;
import it.niedermann.nextcloud.tables.remote.ocs.adapter.OcsAutocompleteSourceListAdapter;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;

@WorkerThread
public class OcsApiProvider<T> extends ApiProvider<T> {

    private static final String API_ENDPOINT_OCS = "/ocs/v2.php/";

    public OcsApiProvider(@NonNull Context context,
                          @NonNull Account account,
                          @NonNull Class<T> clazz) throws NextcloudFilesAppAccountNotFoundException {
        this(context, account, clazz, new GsonBuilder()
                .registerTypeAdapter(ListTypeAdapters.AUTOCOMPLETE_SOURCE.typeToken, new OcsAutocompleteSourceListAdapter()));
//                .registerTypeAdapter(OcsAutocompleteResult.OcsAutocompleteSource[].class, new OcsAutocompleteSourceAdapter()));
    }

    private OcsApiProvider(@NonNull Context context,
                           @NonNull Account account,
                           @NonNull Class<T> clazz,
                           @NonNull GsonBuilder gsonBuilder) throws NextcloudFilesAppAccountNotFoundException {
        super(context, account, clazz, gsonBuilder);
    }

    @Override
    @NonNull
    protected String getEndpoint() {
        return API_ENDPOINT_OCS;
    }

    private enum ListTypeAdapters {
        AUTOCOMPLETE_SOURCE(TypeToken.getParameterized(ArrayList.class, OcsAutocompleteResult.class).getType()),
        ;

        public final Type typeToken;

        private ListTypeAdapters(@NonNull Type typeToken) {
            this.typeToken = typeToken;
        }
    }
}
