package it.niedermann.nextcloud.tables.remote.adapter;

import static java.lang.String.valueOf;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;

public class DataArrayAdapter implements JsonSerializer<Data[]> {

    /**
     * @see TablesAPI#createRow(long, JsonElement)
     */
    @Override
    public JsonElement serialize(@Nullable Data[] data, @Nullable Type typeOfSrc, @Nullable JsonSerializationContext context) {
        final var properties = new JsonObject();
        if (data != null) {
            Arrays.stream(data).forEach(d -> properties.addProperty(valueOf(d.getRemoteColumnId()), d.getValue()));
        }
        return properties;
    }
}