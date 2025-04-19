package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.Objects;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;

public class CreateSelectionCheckColumnV2Dto extends CreateColumnV2Dto {

    /// @noinspection FieldCanBeLocal
    /// API expects property to be present
    private final String selectionOptions = "";
    private final JsonElement selectionDefault;

    public CreateSelectionCheckColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto column) {
        super(tableRemoteId, column);
        this.selectionDefault = column.selectionDefault();
    }

    public String getSelectionOptions() {
        return selectionOptions;
    }

    public JsonElement getSelectionDefault() {
        return selectionDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CreateSelectionCheckColumnV2Dto that = (CreateSelectionCheckColumnV2Dto) o;
        return Objects.equals(selectionDefault, that.selectionDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), selectionDefault);
    }
}
