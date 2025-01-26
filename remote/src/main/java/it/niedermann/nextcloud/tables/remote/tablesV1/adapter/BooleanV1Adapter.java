package it.niedermann.nextcloud.tables.remote.tablesV1.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BooleanV1Adapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

    private static final JsonPrimitive TRUE = new JsonPrimitive(1);
    private static final JsonPrimitive FALSE = new JsonPrimitive(0);

    @Override
    public synchronized JsonElement serialize(Boolean src, Type type, JsonSerializationContext jsonSerializationContext) {
        return Boolean.TRUE.equals(src) ? TRUE : FALSE;
    }

    @Override
    public synchronized Boolean deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        if (jsonElement == null) {
            return false;
        }

        try {
            return jsonElement.getAsBoolean();
        } catch (UnsupportedOperationException | IllegalStateException e) {
            return false;
        }
    }
}