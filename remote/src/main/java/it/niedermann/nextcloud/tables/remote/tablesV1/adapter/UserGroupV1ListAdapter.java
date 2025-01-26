package it.niedermann.nextcloud.tables.remote.tablesV1.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import it.niedermann.nextcloud.tables.remote.tablesV1.model.ColumnRequestV1Dto;

public class UserGroupV1ListAdapter implements JsonSerializer<List<ColumnRequestV1Dto.UserGroupV1Dto>> {

    @Override
    public JsonElement serialize(List<ColumnRequestV1Dto.UserGroupV1Dto> src, Type typeOfSrc, JsonSerializationContext context) {
        return Optional.ofNullable(src)
                .map(context::serialize)
                .map(JsonElement::toString)
                .map(JsonPrimitive::new)
                .map(JsonElement.class::cast)
                .orElse(JsonNull.INSTANCE);
    }
}