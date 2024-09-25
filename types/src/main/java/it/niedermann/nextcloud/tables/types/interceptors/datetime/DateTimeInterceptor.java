package it.niedermann.nextcloud.tables.types.interceptors.datetime;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import it.niedermann.nextcloud.tables.types.interceptors.Interceptor;

public class DateTimeInterceptor implements Interceptor {
    @NonNull
    @Override
    public JsonElement interceptRequest(@NonNull TablesVersion version, @NonNull JsonElement value) {
        if (value.isJsonNull()) {
            return JsonNull.INSTANCE;
        }

        return new JsonPrimitive(TablesAPI.FORMATTER_DATA_DATE_TIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value.getAsString())));
    }

    @NonNull
    @Override
    public JsonElement interceptResponse(@NonNull TablesVersion version, @NonNull JsonElement value) {
        if (value.isJsonNull()) {
            return JsonNull.INSTANCE;
        }

        final var str = value.getAsString();

        // https://github.com/stefan-niedermann/nextcloud-tables/issues/18
        if (version.isLessThanOrEqual(TablesVersion.V_0_5_0)) {
            if ("none".equals(str)) {
                return JsonNull.INSTANCE;
            }
        }

        return TextUtils.isEmpty(str)
                ? JsonNull.INSTANCE
                : new JsonPrimitive(DateTimeFormatter.ISO_DATE_TIME.format(TablesAPI.FORMATTER_DATA_DATE_TIME.parse(str)));
    }
}
