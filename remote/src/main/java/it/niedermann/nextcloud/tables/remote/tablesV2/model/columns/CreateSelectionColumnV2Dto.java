package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;

public class CreateSelectionColumnV2Dto extends CreateColumnV2Dto {

    private final String selectionOptions;
    private final JsonElement selectionDefault;

    public CreateSelectionColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto column) {
        super(tableRemoteId, column);

        if (column.selectionOptions() == null) {
            this.selectionOptions = "";

        } else {
            final var arr = new JsonArray();
            for (final var option : column.selectionOptions()) {
                final var obj = new JsonObject();
                obj.addProperty("id", option.remoteId());
                obj.addProperty("label", option.label());
                arr.add(obj);
            }
            this.selectionOptions = arr.toString();
        }

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
        CreateSelectionColumnV2Dto that = (CreateSelectionColumnV2Dto) o;
        return Objects.equals(selectionOptions, that.selectionOptions) && Objects.equals(selectionDefault, that.selectionDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), selectionOptions, selectionDefault);
    }
}
