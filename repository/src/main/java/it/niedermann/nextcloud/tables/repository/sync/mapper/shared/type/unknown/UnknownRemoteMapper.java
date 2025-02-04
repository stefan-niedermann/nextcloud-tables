package it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.unknown;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.repository.sync.mapper.shared.type.DataV1Mapper;

public class UnknownRemoteMapper extends DataV1Mapper {

    @Override
    @NonNull
    public JsonElement toRemoteValue(@NonNull FullData entity,
                                     @NonNull EDataType dataType,
                                     @NonNull TablesVersion version) {
        return Optional
                .of(entity)
                .map(FullData::getData)
                .map(Data::getValue)
                .map(Value::getStringValue)
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
                .filter(v -> v != JsonNull.INSTANCE)
                .map(JsonElement::toString)
                .ifPresent(fullData.getData().getValue()::setStringValue);
    }
}
