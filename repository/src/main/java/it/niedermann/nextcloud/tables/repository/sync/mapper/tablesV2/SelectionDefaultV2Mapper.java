package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV2;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.model.SelectionDefault;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class SelectionDefaultV2Mapper implements Mapper<JsonElement, SelectionDefault> {

    @NonNull
    @Override
    public JsonElement toDto(@NonNull SelectionDefault entity) {
        return entity.getValue();
    }

    @NonNull
    @Override
    public SelectionDefault toEntity(@NonNull JsonElement dto) {
        return new SelectionDefault(dto);
    }
}
