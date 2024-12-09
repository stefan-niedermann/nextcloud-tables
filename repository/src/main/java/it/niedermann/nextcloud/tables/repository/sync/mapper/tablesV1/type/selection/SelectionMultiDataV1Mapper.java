package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.DataV1Mapper;

public class SelectionMultiDataV1Mapper extends DataV1Mapper {

    @NonNull
    @Override
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        final var selectionRemoteIds = new JsonArray();

        Optional
                .of(entity)
                .map(FullData::getSelectionOptions)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(AbstractRemoteEntity::getRemoteId)
                .forEach(selectionRemoteIds::add);

        return selectionRemoteIds;
    }

    @Override
    public @NonNull FullData toData(@Nullable JsonElement dto,
                                    @Nullable Long columnRemoteId,
                                    @NonNull EDataType dataTypeAccordingToLocalColumn,
                                    @NonNull TablesVersion version) {
        final var fullData = new FullData();
        final var data = new Data();

        Optional.ofNullable(dto)
                .filter(JsonElement::isJsonArray)
                .map(JsonElement::getAsJsonArray)
                .map(JsonArray::asList)
                .map(this::mapToSelectionOptions)
                .ifPresent(fullData::setSelectionOptions);

        fullData.setData(data);
        fullData.setDataType(dataTypeAccordingToLocalColumn);

        Optional.ofNullable(columnRemoteId)
                .ifPresent(data::setRemoteColumnId);

        return fullData;
    }

    @NonNull
    private List<SelectionOption> mapToSelectionOptions(@NonNull List<JsonElement> jsonArray) {
        return jsonArray
                .stream()
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsLong)
                .map(remoteId -> {
                    final var selectionOption = new SelectionOption();
                    selectionOption.setRemoteId(remoteId);
                    return selectionOption;
                })
                .collect(Collectors.toUnmodifiableList());
    }
}