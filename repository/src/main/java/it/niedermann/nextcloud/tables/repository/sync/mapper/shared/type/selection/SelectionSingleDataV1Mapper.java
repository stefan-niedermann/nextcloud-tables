package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.DataV1Mapper;

public class SelectionSingleDataV1Mapper extends DataV1Mapper {

    @NonNull
    @Override
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        return Optional
                .of(entity)
                .map(FullData::getSelectionOptions)
                .map(List::stream)
                .flatMap(Stream::findAny)
                .map(SelectionOption::getRemoteId)
                .map(JsonPrimitive::new)
                .map(JsonElement.class::cast)
                .orElse(JsonNull.INSTANCE);
    }

    @Override
    protected void toFullData(@NonNull FullData fullData,
                              @Nullable JsonElement value,
                              @NonNull FullColumn fullColumn,
                              @NonNull TablesVersion version) {
        Optional.ofNullable(value)
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsLong)
                .map(remoteId -> fullColumn.getSelectionOptions()
                        .stream()
                        .filter(selectionOption -> Objects.equals(remoteId, selectionOption.getRemoteId()))
                        .findAny())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(List::of)
                .ifPresent(fullData::setSelectionOptions);
    }
}