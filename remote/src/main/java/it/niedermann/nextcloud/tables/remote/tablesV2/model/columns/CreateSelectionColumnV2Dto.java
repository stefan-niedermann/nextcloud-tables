package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.SelectionOptionV2Dto;

public class CreateSelectionColumnV2Dto extends CreateColumnV2Dto {

    private final List<SelectionOptionV2Dto> selectionOptions;
    private final JsonElement selectionDefault;

    public CreateSelectionColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto column) {
        super(tableRemoteId, column);
        this.selectionOptions = column.selectionOptions();
        this.selectionDefault = column.selectionDefault();
    }

    public List<SelectionOptionV2Dto> getSelectionOptions() {
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
        CreateSelectionColumnV2Dto that = (CreateSelectionColumnV2Dto) o;
        return Objects.equals(selectionOptions, that.selectionOptions) && Objects.equals(selectionDefault, that.selectionDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), selectionOptions, selectionDefault);
    }
}
