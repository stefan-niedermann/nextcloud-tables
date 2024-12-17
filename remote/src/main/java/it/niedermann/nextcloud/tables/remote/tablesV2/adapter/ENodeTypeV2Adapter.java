package it.niedermann.nextcloud.tables.remote.tablesV2.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Optional;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;


public class ENodeTypeV2Adapter implements JsonSerializer<ENodeTypeV2Dto>, JsonDeserializer<ENodeTypeV2Dto> {

    @Override
    public ENodeTypeV2Dto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Optional.ofNullable(json)
                .map(JsonElement::getAsString)
                .map(ENodeTypeV2Dto::findByString)
                .orElse(null);
    }

    @Override
    public JsonElement serialize(ENodeTypeV2Dto src, Type typeOfSrc, JsonSerializationContext context) {
        return Optional.ofNullable(src)
                .map(ENodeTypeV2Dto::toString)
                .map(JsonPrimitive::new)
                .orElse(null);
    }
}