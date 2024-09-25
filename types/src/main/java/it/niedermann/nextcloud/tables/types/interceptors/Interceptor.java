package it.niedermann.nextcloud.tables.types.interceptors;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;

public interface Interceptor {

    /**
     * @return {@link JsonElement} representing the {@link Data#getValue()}
     */
    @NonNull
    default JsonElement interceptRequest(@NonNull TablesVersion tablesVersion, @NonNull JsonElement value) {
        return value;
    }


    @NonNull
    default JsonElement interceptResponse(@NonNull TablesVersion version, @NonNull JsonElement value) {
        return value;
    }
}
