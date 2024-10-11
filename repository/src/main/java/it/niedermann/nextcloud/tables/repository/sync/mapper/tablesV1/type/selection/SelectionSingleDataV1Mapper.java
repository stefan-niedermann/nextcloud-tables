package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.DataV1Mapper;

public class SelectionSingleDataV1Mapper extends DataV1Mapper {

    @NonNull
    @Override
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        return Optional.ofNullable(entity.getSelectionOptions())
                .map(List::stream)
                .flatMap(Stream::findAny)
                .map(SelectionOption::getRemoteId)
                .map(JsonPrimitive::new)
                .map(JsonElement.class::cast)
                .orElse(JsonNull.INSTANCE);
    }

    @Override
    public @NonNull FullData toData(@Nullable JsonElement dto,
                                    @Nullable Long columnRemoteId,
                                    @NonNull EDataType dataTypeAccordingToLocalColumn,
                                    @NonNull TablesVersion version) {
        final var fullData = new FullData();
        final var data = new Data();

        Optional.ofNullable(dto)
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsLong)
                .map(List::of)
                .map(selectionOptionRemoteIds -> selectionOptionRemoteIds
                        .stream()
                        .map(remoteId -> {
                            final var selectionOption = new SelectionOption();
                            selectionOption.setRemoteId(remoteId);
                            return selectionOption;
                        })
                        .collect(Collectors.toUnmodifiableList())
                )
                .ifPresent(fullData::setSelectionOptions);

        fullData.setData(data);
        fullData.setDataType(dataTypeAccordingToLocalColumn);

        Optional.ofNullable(columnRemoteId)
                .ifPresent(data::setRemoteColumnId);

        return fullData;
    }
}