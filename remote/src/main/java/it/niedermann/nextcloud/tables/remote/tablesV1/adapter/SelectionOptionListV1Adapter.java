package it.niedermann.nextcloud.tables.remote.tablesV1.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.remote.tablesV1.model.SelectionOptionV1Dto;

public class SelectionOptionListV1Adapter implements JsonSerializer<List<SelectionOptionV1Dto>>, JsonDeserializer<List<SelectionOptionV1Dto>> {

    @NonNull
    private final Gson gson;

    public SelectionOptionListV1Adapter() {
        this(new GsonBuilder().create());
    }

    private SelectionOptionListV1Adapter(@NonNull Gson gson) {
        this.gson = gson;
    }

    @Override
    public JsonElement serialize(List<SelectionOptionV1Dto> src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(gson.toJson(src));
    }

    @Override
    public List<SelectionOptionV1Dto> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.isJsonArray() || json.isJsonObject()
                ? deserialize(json)
                : Collections.emptyList();
    }

    @NonNull
    public List<SelectionOptionV1Dto> deserialize(@NonNull JsonElement json) throws JsonParseException {
        if (json.isJsonObject()) {
            return deserialize(json.getAsJsonObject())
                    .map(List::of)
                    .orElse(Collections.emptyList());
        } else if (json.isJsonArray()) {
            return json.getAsJsonArray()
                    .asList()
                    .stream()
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .map(this::deserialize)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toUnmodifiableList());
        }

        return Collections.emptyList();
    }

    @NonNull
    private Optional<SelectionOptionV1Dto> deserialize(@Nullable JsonObject object) {
        if (object == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(gson.fromJson(object, SelectionOptionV1Dto.class));
        } catch (JsonSyntaxException e) {
            return Optional.empty();
        }
    }
}
