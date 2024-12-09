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

import it.niedermann.nextcloud.tables.remote.tablesV2.model.EUserGroupTypeV2Dto;

public class EUserGroupV2Adapter implements JsonSerializer<EUserGroupTypeV2Dto>, JsonDeserializer<EUserGroupTypeV2Dto> {

    @Override
    public EUserGroupTypeV2Dto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Optional.ofNullable(json)
                .map(JsonElement::getAsInt)
                .map(EUserGroupTypeV2Dto::findByRemoteId)
                .orElse(null);
    }

    @Override
    public JsonElement serialize(EUserGroupTypeV2Dto src, Type typeOfSrc, JsonSerializationContext context) {
        return Optional.ofNullable(src)
                .map(EUserGroupTypeV2Dto::getRemoteId)
                .map(JsonPrimitive::new)
                .orElse(null);
    }
}