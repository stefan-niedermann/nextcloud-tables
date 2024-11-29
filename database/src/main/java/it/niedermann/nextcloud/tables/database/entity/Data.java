package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.model.Value;


@Entity(
        inheritSuperIndices = true,
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Row.class,
                        parentColumns = "id",
                        childColumns = "rowId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Column.class,
                        parentColumns = "id",
                        childColumns = "columnId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Column.class,
                        parentColumns = {"accountId", "remoteId"},
                        childColumns = {"accountId", "remoteColumnId"},
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"accountId", "rowId", "columnId"}, unique = true),
                @Index(value = {"accountId", "rowId", "remoteColumnId"}, unique = true),
        }
)
public class Data extends AbstractAccountRelatedEntity {

    private long rowId;
    private long columnId;
    private long remoteColumnId;

    @NonNull
    @Embedded(prefix = "data_")
    private Value value;

    public Data() {
        this.value = new Value();
    }

    @Ignore
    public Data(@NonNull Data data) {
        this.id = data.getId();
        this.rowId = data.getRowId();
        this.value = data.getValue();
        this.remoteColumnId = data.getRemoteColumnId();
        this.accountId = data.getAccountId();
        this.columnId = data.getColumnId();
        this.eTag = data.getETag();
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public long getRemoteColumnId() {
        return remoteColumnId;
    }

    public void setRemoteColumnId(long remoteColumnId) {
        this.remoteColumnId = remoteColumnId;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    @NonNull
    public Value getValue() {
        return value;
    }

    public void setValue(@NonNull Value value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Data data = (Data) o;
        return columnId == data.columnId && remoteColumnId == data.remoteColumnId && rowId == data.rowId && Objects.equals(value, data.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), columnId, remoteColumnId, rowId, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "[Row: " + getRowId() + " / Column: " + getColumnId() + "]: " + getValue();
    }
}
