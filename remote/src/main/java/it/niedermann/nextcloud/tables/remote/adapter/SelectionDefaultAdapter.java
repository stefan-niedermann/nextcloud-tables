package it.niedermann.nextcloud.tables.remote.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import it.niedermann.nextcloud.tables.database.model.SelectionDefault;

public class SelectionDefaultAdapter implements JsonSerializer<SelectionDefault>, JsonDeserializer<SelectionDefault> {

    @Override
    public synchronized JsonElement serialize(SelectionDefault selectionDefault, Type type, JsonSerializationContext jsonSerializationContext) {
        return selectionDefault == null ? JsonNull.INSTANCE : selectionDefault.getValue();
    }

    @Override
    public synchronized SelectionDefault deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        return new SelectionDefault(jsonElement);
    }
}