package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.number;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.DataV1Mapper;

public class NumberRemoteMapper extends DataV1Mapper {

    @NonNull
    @Override
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        return Optional.of(entity.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .map(Math::round)
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
        fullData.setDataType(dataTypeAccordingToLocalColumn);

        final var data = new Data();

        Optional.ofNullable(dto)
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsDouble)
                .ifPresent(data.getValue()::setDoubleValue);

        fullData.setData(data);
        fullData.setDataType(dataTypeAccordingToLocalColumn);

        Optional.ofNullable(columnRemoteId)
                .ifPresent(data::setRemoteColumnId);

        return fullData;
    }
}
