package it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.DataV1Mapper;

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
                .map(SelectionOption::getRemoteId)
                .forEach(selectionRemoteIds::add);

        return selectionRemoteIds;
    }

    @Override
    protected void toFullData(@NonNull FullData fullData,
                              @Nullable JsonElement value,
                              @NonNull FullColumn fullColumn,
                              @NonNull TablesVersion version) {
        Optional.ofNullable(value)
                .filter(JsonElement::isJsonArray)
                .map(JsonElement::getAsJsonArray)
                .map(JsonArray::asList)
                .map(selectionOptionRemoteIds -> this.mapToSelectionOptions(selectionOptionRemoteIds, fullColumn.getSelectionOptions()))
                .ifPresent(fullData::setSelectionOptions);
    }

    @NonNull
    private List<SelectionOption> mapToSelectionOptions(@NonNull Collection<JsonElement> selectionOptionRemoteIds,
                                                        @NonNull Collection<SelectionOption> selectionOptions) {
        return selectionOptionRemoteIds
                .stream()
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsLong)
                .map(remoteId -> selectionOptions
                        .stream()
                        .filter(selectionOption -> Objects.equals(remoteId, selectionOption.getRemoteId()))
                        .findAny())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());
    }
}