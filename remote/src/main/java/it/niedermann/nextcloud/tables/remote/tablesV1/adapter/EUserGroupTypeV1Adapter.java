package it.niedermann.nextcloud.tables.remote.tablesV1.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Optional;

import it.niedermann.nextcloud.tables.remote.tablesV1.model.EUserGroupTypeV1Dto;

public class EUserGroupTypeV1Adapter implements JsonSerializer<EUserGroupTypeV1Dto>, JsonDeserializer<EUserGroupTypeV1Dto> {

    @Override
    public EUserGroupTypeV1Dto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Optional.ofNullable(json)
                .map(JsonElement::getAsInt)
                .map(EUserGroupTypeV1Dto::findByRemoteId)
                .orElse(null);
    }

    @Override
    public JsonElement serialize(EUserGroupTypeV1Dto src, Type typeOfSrc, JsonSerializationContext context) {
        return Optional.ofNullable(src)
                .map(EUserGroupTypeV1Dto::getRemoteId)
                .map(JsonPrimitive::new)
                .orElse(null);
    }
}