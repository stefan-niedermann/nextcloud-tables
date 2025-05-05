package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

@Entity(
        primaryKeys = {"columnId", "selectionOptionId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Column.class,
                        parentColumns = "id",
                        childColumns = "columnId",
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
                @Index(value = {"columnId"}),
                @Index(value = {"selectionOptionId"})
        }
)
public class DefaultValueSelectionOptionCrossRef {

    public long columnId;
    public long selectionOptionId;

    public DefaultValueSelectionOptionCrossRef() {
        // Default constructor
    }

    @Ignore
    public DefaultValueSelectionOptionCrossRef(long columnId, long selectionOptionId) {
        this.columnId = columnId;
        this.selectionOptionId = selectionOptionId;
    }

    public static DefaultValueSelectionOptionCrossRef from(@NonNull SelectionOption selectionOption) {
        return new DefaultValueSelectionOptionCrossRef(selectionOption.getColumnId(), selectionOption.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultValueSelectionOptionCrossRef that = (DefaultValueSelectionOptionCrossRef) o;
        return columnId == that.columnId && selectionOptionId == that.selectionOptionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnId, selectionOptionId);
    }
}

