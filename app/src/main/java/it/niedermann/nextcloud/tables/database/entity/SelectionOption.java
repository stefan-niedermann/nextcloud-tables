package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Objects;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Column.class,
                        parentColumns = "id",
                        childColumns = "columnId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"accountId", "id", "remoteId"}, unique = true),
                @Index(value = {"accountId", "id"}, unique = true),
                @Index(name = "IDX_SELECTION_OPTION_COLUMN_ID", value = "columnId")
        }
)
public class SelectionOption extends AbstractRemoteEntity {

    private long columnId;
    private String label;

    public SelectionOption() {
        // Default constructor
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SelectionOption that = (SelectionOption) o;
        return columnId == that.columnId && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), columnId, label);
    }
}
