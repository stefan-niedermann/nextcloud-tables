package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;

import it.niedermann.nextcloud.tables.database.model.SelectionDefault;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class SelectionDefaultV1Mapper implements Mapper<JsonElement, SelectionDefault> {

    @NonNull
    @Override
    public JsonElement toDto(@NonNull SelectionDefault entity) {
        return entity.getValue();
    }

    @NonNull
    @Override
    public SelectionDefault toEntity(@NonNull JsonElement dto) {
        if (dto == JsonNull.INSTANCE) {
            return new SelectionDefault();
        }

        final var value = JsonParser.parseString(dto.getAsString());
        return new SelectionDefault(value);
    }
}
