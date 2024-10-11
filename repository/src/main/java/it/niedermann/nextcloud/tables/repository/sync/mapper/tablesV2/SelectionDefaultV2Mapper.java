package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class SelectionDefaultV2Mapper implements Mapper<JsonElement, List<SelectionOption>> {

    @NonNull
    public JsonElement toDto(@NonNull EDataType dataType,
                             @NonNull List<SelectionOption> entity) {
        return switch (dataType) {
            case SELECTION -> entity.isEmpty() ? JsonNull.INSTANCE : toDto(entity);
            case SELECTION_MULTI ->
                    entity.isEmpty() ? JsonNull.INSTANCE : toDto(List.of(entity.get(0)));
            default ->
                    throw new IllegalStateException("Only " + EDataType.SELECTION + " and " + EDataType.SELECTION_MULTI + " are allowed, but got: " + dataType);
        };
    }

    @NonNull
    @Override
    public JsonElement toDto(@NonNull List<SelectionOption> entity) {
        if (entity.isEmpty()) {
            return JsonNull.INSTANCE;
        } else if (entity.size() > 1) {
            final var arr = new JsonArray();
            entity
                    .stream()
                    .map(SelectionOption::getRemoteId)
                    .filter(Objects::nonNull)
                    .map(JsonPrimitive::new)
                    .map(JsonElement.class::cast)
                    .forEach(arr::add);
            return arr;
        } else {
            return entity
                    .stream()
                    .findAny()
                    .map(SelectionOption::getRemoteId)
                    .map(JsonPrimitive::new)
                    .map(JsonElement.class::cast)
                    .orElse(JsonNull.INSTANCE);
        }
    }

    @NonNull
    @Override
    public List<SelectionOption> toEntity(@NonNull JsonElement dto) {
        if (dto.isJsonNull()) {
            return Collections.emptyList();
        }

        if (dto.isJsonObject()) {
            final var obj = dto.getAsJsonObject();
            if (obj.has("id") && obj.has("label")) {
                return List.of(new SelectionOption());
            }
        } else if (dto.isJsonArray()) {
            return dto.getAsJsonArray()
                    .asList()
                    .stream()
                    .map(this::toEntity)
                    .flatMap(List::stream)
                    .collect(Collectors.toUnmodifiableList());
        }

        return Collections.emptyList();
    }
}
