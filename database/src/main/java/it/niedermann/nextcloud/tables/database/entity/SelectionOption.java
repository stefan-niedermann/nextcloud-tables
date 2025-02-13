package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

@Entity(
        inheritSuperIndices = true,
        foreignKeys = {
                @ForeignKey(
                        entity = Column.class,
                        parentColumns = "id",
                        childColumns = "columnId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = "remoteId"),
                @Index(value = {"columnId", "remoteId"}, unique = true),
        }
)
public class SelectionOption extends AbstractEntity implements Comparable<SelectionOption> {

    /// Unique per [Column]
    @Nullable
    private Long remoteId;

    private long columnId;

    private String label;

    public SelectionOption() {
        // Default constructor
    }

    @Ignore
    public SelectionOption(@Nullable Long remoteId, String label) {
        this.remoteId = remoteId;
        this.label = label;
    }

    @Ignore
    public SelectionOption(@NonNull SelectionOption selectionOption) {
        super(selectionOption);
        this.remoteId = selectionOption.getRemoteId();
        this.columnId = selectionOption.getColumnId();
        this.label = selectionOption.getLabel();
    }

    @Nullable
    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(@Nullable Long remoteId) {
        this.remoteId = remoteId;
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

    @NonNull
    @Override
    public String toString() {
        return "SelectionOption{" +
                "remoteId=" + remoteId +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SelectionOption that = (SelectionOption) o;
        return columnId == that.columnId && Objects.equals(remoteId, that.remoteId) && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteId, columnId, label);
    }

    @Override
    public int compareTo(SelectionOption o) {
        if (o == null) {
            return 1;
        }

        final var otherRemoteId = o.getRemoteId();
        return otherRemoteId == null
                ? remoteId == null ? 0 : 1
                : remoteId == null ? -1 : remoteId.compareTo(otherRemoteId);
    }
}

