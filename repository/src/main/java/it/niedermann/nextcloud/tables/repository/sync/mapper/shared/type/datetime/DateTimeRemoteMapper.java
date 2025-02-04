package it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.datetime;

import static java.util.function.Predicate.not;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.DataV1Mapper;

public class DateTimeRemoteMapper extends DataV1Mapper {

    @NonNull
    @Override
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        return Optional
                .of(entity)
                .map(FullData::getData)
                .map(Data::getValue)
                .map(Value::getInstantValue)
                .map(instant -> instant.atZone(ZoneId.systemDefault()))
                .map(TablesV1API.FORMATTER_DATA_DATE_TIME::format)
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
                .map(JsonElement::getAsString)
                // https://github.com/stefan-niedermann/nextcloud-tables/issues/18
                .filter(stringValue -> version.isGreaterThan(TablesVersion.V_0_5_0) && !"none".equals(stringValue))
                .filter(not(String::isBlank))
                .map(TablesV1API.FORMATTER_DATA_DATE_TIME::parse)
                .map(LocalDateTime::from)
                .map(ldt -> ldt.atZone(ZoneId.systemDefault()))
                .map(Instant::from)
                .ifPresent(fullData.getData().getValue()::setInstantValue);
    }
}
