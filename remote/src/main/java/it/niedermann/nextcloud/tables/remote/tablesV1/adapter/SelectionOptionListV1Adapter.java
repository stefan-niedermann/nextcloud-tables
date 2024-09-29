package it.niedermann.nextcloud.tables.remote.tablesV1.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.remote.tablesV1.model.SelectionOptionV1Dto;

public class SelectionOptionListV1Adapter implements JsonSerializer<List<SelectionOptionV1Dto>>, JsonDeserializer<List<SelectionOptionV1Dto>> {

    @Override
    public JsonElement serialize(List<SelectionOptionV1Dto> src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(new GsonBuilder().create().toJson(src));
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

        if (object.has("id") && object.has("label")) {
            final var idElement = object.get("id");
            final var labelElement = object.get("label");
            if (idElement.isJsonPrimitive() && labelElement.isJsonPrimitive()) {
                try {
                    final var id = idElement.getAsLong();
                    final var label = labelElement.getAsString();
                    if (id > 0 & !TextUtils.isEmpty(label)) {
                        return Optional.of(new SelectionOptionV1Dto(id, label));
                    }
                } catch (UnsupportedOperationException | NumberFormatException |
                         IllegalStateException ignored) {
                    return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }
}
