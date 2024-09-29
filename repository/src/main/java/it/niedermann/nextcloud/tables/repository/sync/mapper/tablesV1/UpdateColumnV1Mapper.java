package it.niedermann.nextcloud.tables.repository.sync.mapper.tablesV1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.SelectionDefault;
import it.niedermann.nextcloud.tables.remote.tablesV1.model.UpdateColumnV1Dto;
import it.niedermann.nextcloud.tables.repository.sync.mapper.Mapper;

public class UpdateColumnV1Mapper implements Mapper<UpdateColumnV1Dto, Column> {

    private final SelectionOptionV1Mapper mapper = new SelectionOptionV1Mapper();

    @NonNull
    @Override
    public UpdateColumnV1Dto toDto(@NonNull Column entity) {
        return new UpdateColumnV1Dto(
                Objects.requireNonNullElse(entity.getTitle(), ""),
                entity.isMandatory(),
                entity.getDescription(),
                entity.getNumberPrefix(),
                entity.getNumberSuffix(),
                entity.getNumberDefault(),
                entity.getNumberMin(),
                entity.getNumberMax(),
                entity.getNumberDecimals(),
                entity.getTextDefault(),
                entity.getTextAllowedPattern(),
                entity.getTextMaxLength(),
                mapper.toDtoList(entity.getSelectionOptions()),
                serializeSelectionDefault(entity.getSelectionDefault()),
                entity.getDatetimeDefault());
    }

    @NonNull
    private String serializeSelectionDefault(@Nullable SelectionDefault selectionDefault) {
        return serializeSelectionDefault(
                selectionDefault == null ? null : selectionDefault.getValue());
    }

    @NonNull
    private String serializeSelectionDefault(@Nullable JsonElement value) {
        if (value == null) {
            return "";
        }

        return new JsonPrimitive(value.toString()).toString();
    }

    @NonNull
    @Override
    public Column toEntity(@NonNull UpdateColumnV1Dto dto) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
