package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.DefaultValueSelectionOptionCrossRef;
import it.niedermann.nextcloud.tables.database.entity.DefaultValueUserGroupCrossRef;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;

public class FullColumn implements Serializable, Comparable<FullColumn> {

    @NonNull
    @Embedded
    private Column column;

    @NonNull
    @Relation(
            parentColumn = "id",
            entityColumn = "columnId",
            entity = SelectionOption.class
    )
    private List<SelectionOption> selectionOptions;

    @NonNull
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = DefaultValueSelectionOptionCrossRef.class,
                    parentColumn = "columnId",
                    entityColumn = "selectionOptionId"
            )
    )
    public List<SelectionOption> defaultSelectionOptions;

    @NonNull
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = DefaultValueUserGroupCrossRef.class,
                    parentColumn = "columnId",
                    entityColumn = "userGroupId"
            )
    )
    public List<UserGroup> defaultUserGroups;

    public FullColumn() {
        this(new Column());
    }

    @Ignore
    public FullColumn(@NonNull Column column) {
        this.column = column;
        this.selectionOptions = Collections.emptyList();
        this.defaultSelectionOptions = Collections.emptyList();
        this.defaultUserGroups = Collections.emptyList();
    }

    @NonNull
    public Column getColumn() {
        return column;
    }

    public void setColumn(@NonNull Column column) {
        this.column = column;
    }

    @NonNull
    public List<SelectionOption> getSelectionOptions() {
        return selectionOptions;
    }

    public void setSelectionOptions(@NonNull List<SelectionOption> selectionOptions) {
        this.selectionOptions = selectionOptions;
    }

    @NonNull
    public List<SelectionOption> getDefaultSelectionOptions() {
        return defaultSelectionOptions;
    }

    public void setDefaultSelectionOptions(@NonNull List<SelectionOption> defaultSelectionOptions) {
        this.defaultSelectionOptions = defaultSelectionOptions;
    }

    @NonNull
    public List<UserGroup> getDefaultUserGroups() {
        return defaultUserGroups;
    }

    public void setDefaultUserGroups(@NonNull List<UserGroup> defaultUserGroups) {
        this.defaultUserGroups = defaultUserGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullColumn that = (FullColumn) o;
        return Objects.equals(column, that.column) && Objects.equals(selectionOptions, that.selectionOptions) && Objects.equals(defaultSelectionOptions, that.defaultSelectionOptions) && Objects.equals(defaultUserGroups, that.defaultUserGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, selectionOptions, defaultSelectionOptions, defaultUserGroups);
    }

    @Override
    public int compareTo(FullColumn o) {
        return column.compareTo(o.getColumn());
    }
}
