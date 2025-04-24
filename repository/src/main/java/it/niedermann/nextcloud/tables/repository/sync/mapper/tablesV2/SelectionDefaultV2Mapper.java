package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.EDataType;

public class SelectionDefaultV2Mapper {

    @NonNull
    public JsonElement toDto(@NonNull EDataType dataType,
                             @NonNull List<SelectionOption> entity) {
        return switch (dataType) {
            case SELECTION -> entity.isEmpty()
                    ? JsonNull.INSTANCE
                    : toDto(entity.get(0));
            case SELECTION_MULTI -> entity.isEmpty()
                    ? JsonNull.INSTANCE
                    : toDto(entity);
            default ->
                    throw new IllegalStateException("Only " + EDataType.SELECTION + " and " + EDataType.SELECTION_MULTI + " are allowed, but got: " + dataType);
        };
    }

    @NonNull
    public JsonElement toDto(@NonNull List<SelectionOption> entity) {
        if (entity.isEmpty()) {
            return JsonNull.INSTANCE;
        }

        final var arr = new JsonArray();
        entity
                .stream()
                .map(this::toDto)
                .forEach(arr::add);
        return new JsonPrimitive(arr.toString());
    }

    @NonNull
    private JsonElement toDto(@NonNull SelectionOption entity) {
        if (entity.getRemoteId() == null) {
            throw new IllegalStateException("Expected " + SelectionOption.class.getSimpleName() + "#remoteId to be set by client before push.");
        }

        return new JsonPrimitive(String.valueOf(entity.getRemoteId()));
    }

    @NonNull
    public List<Long> selectionMultiToEntity(@Nullable JsonElement dto) {
        return Optional.ofNullable(dto)
                .filter(JsonElement::isJsonArray)
                .map(JsonArray.class::cast)
                .map(JsonArray::asList)
                .map(Collection::stream)
                .map(stream -> stream
                        .filter(JsonElement::isJsonPrimitive)
                        .map(JsonElement::getAsJsonPrimitive)
                        .filter(JsonPrimitive::isString)
                        .map(JsonPrimitive::getAsLong)
                        .toList())
                .orElseGet(Collections::emptyList);
    }

    @Nullable
    public Long selectionSingleToEntity(@Nullable JsonElement dto) {
        return Optional.ofNullable(dto)
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonPrimitive.class::cast)
                .filter(JsonPrimitive::isNumber)
                .map(JsonPrimitive::getAsLong)
                .orElse(null);
    }

    public boolean selectionCheckToEntity(@Nullable JsonElement dto) {
        return Optional.ofNullable(dto)
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonPrimitive.class::cast)
                .filter(JsonPrimitive::isString)
                .map(JsonPrimitive::getAsBoolean)
                .orElse(false);
    }
}
