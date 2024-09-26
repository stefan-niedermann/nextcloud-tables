package it.niedermann.nextcloud.tables.remote.model.columns;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.SelectionDefault;

public class SelectionColumn extends AbstractColumn {

    private final List<SelectionOption> selectionOptions;
    private final SelectionDefault selectionDefault;

    public SelectionColumn(long tableRemoteId, @NonNull Column column) {
        super(tableRemoteId, column);
        this.selectionOptions = column.getSelectionOptions();
        this.selectionDefault = column.getSelectionDefault();
    }

    public List<SelectionOption> getSelectionOptions() {
        return selectionOptions;
    }

    public SelectionDefault getSelectionDefault() {
        return selectionDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SelectionColumn that = (SelectionColumn) o;
        return Objects.equals(selectionOptions, that.selectionOptions) && Objects.equals(selectionDefault, that.selectionDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), selectionOptions, selectionDefault);
    }
}
