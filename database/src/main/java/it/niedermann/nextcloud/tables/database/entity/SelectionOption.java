package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Locale;
import java.util.Objects;

@Entity(
        /// The [#remoteId] of [SelectionOption]s is unique per [Column] but not per [#accountId].
        /// Inheriting the unique index on [#remoteId] and [#accountId] is therefore not eligible.
        inheritSuperIndices = false,
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
                @Index(value = "accountId"),
                @Index(value = "columnId"),
                @Index(value = {"remoteId", "columnId"}, unique = true),
                @Index(value = "status"),
        }
)
public class SelectionOption extends AbstractRemoteEntity implements Comparable<SelectionOption> {

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

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[%d] %s", remoteId, label);
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

