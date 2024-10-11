package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.datetime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1.type.DataV1Mapper;

public class DateRemoteMapper extends DataV1Mapper {

    @NonNull
    @Override
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        return Optional
                .of(entity)
                .map(FullData::getData)
                .map(Data::getValue)
                .map(Value::getStringValue)
                .map(DateTimeFormatter.ISO_LOCAL_DATE::parse)
                .map(TablesV1API.FORMATTER_DATA_DATE::format)
                .map(JsonPrimitive::new)
                .map(JsonElement.class::cast)
                .orElse(new JsonPrimitive(""));
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
                .map(JsonElement::getAsString)
                // https://github.com/stefan-niedermann/nextcloud-tables/issues/18
                .filter(value -> version.isLessThanOrEqual(TablesVersion.V_0_5_0) && !"none".equals(value))
                .map(TablesV1API.FORMATTER_DATA_DATE::parse)
                .map(LocalDate::from)
                .ifPresent(data.getValue()::setDateValue);

        fullData.setData(data);
        fullData.setDataType(dataTypeAccordingToLocalColumn);

        Optional.ofNullable(columnRemoteId)
                .ifPresent(data::setRemoteColumnId);

        return fullData;
    }
}
