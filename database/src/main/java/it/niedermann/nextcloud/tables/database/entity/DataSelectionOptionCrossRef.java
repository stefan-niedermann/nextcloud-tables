package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Objects;

@Entity(
        primaryKeys = {"dataId", "selectionOptionId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Data.class,
                        parentColumns = "id",
                        childColumns = "dataId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = SelectionOption.class,
                        parentColumns = "id",
                        childColumns = "selectionOptionId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"dataId"}),
                @Index(value = {"selectionOptionId"})
        }
)
public class DataSelectionOptionCrossRef {

    public long dataId;
    public long selectionOptionId;

    public DataSelectionOptionCrossRef() {
        // Default constructor
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSelectionOptionCrossRef that = (DataSelectionOptionCrossRef) o;
        return dataId == that.dataId && selectionOptionId == that.selectionOptionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataId, selectionOptionId);
    }
}

