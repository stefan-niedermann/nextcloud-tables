package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;

public class FullRow implements Serializable, Comparable<FullRow> {

    @NonNull
    @Embedded
    private Row row;

    @NonNull
    @Relation(
            parentColumn = "id",
            entityColumn = "rowId",
            entity = Data.class
    )
    private List<FullData> fullData;

    public FullRow() {
        this.row = new Row();
        this.fullData = Collections.emptyList();
    }

    @NonNull
    public Row getRow() {
        return row;
    }

    public void setRow(@NonNull Row row) {
        this.row = row;
    }

    @NonNull
    public List<FullData> getFullData() {
        return fullData;
    }

    public void setFullData(@NonNull List<FullData> fullData) {
        this.fullData = fullData;
    }

    @NonNull
    @Override
    public String toString() {
        return row.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullRow fullRow = (FullRow) o;
        return Objects.equals(row, fullRow.row) && Objects.equals(fullData, fullRow.fullData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, fullData);
    }

    @Override
    public int compareTo(FullRow o) {
        return row.compareTo(o.getRow());
    }
}
