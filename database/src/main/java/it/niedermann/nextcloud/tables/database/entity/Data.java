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
                // In case a LinkValue entry gets deleted (for example after removing available
                // SearchProviders after synchronization), the value needs to be set `null` here.
                @ForeignKey(
                        entity = LinkValue.class,
                        parentColumns = "dataId",
                        childColumns = "linkValueRef",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index(value = "columnId"),
                @Index(value = {"linkValueRef"}, unique = true),
                @Index(value = {"rowId", "columnId"}, unique = true),
                @Index(value = {"rowId", "remoteColumnId"}, unique = true),
        }
)
public class Data extends AbstractEntity {

    private long rowId;
    private long columnId;
    private long remoteColumnId;

    @NonNull
    @Embedded
    private Value value;

    public Data() {
        this.value = new Value();
    }

    @Ignore
    public Data(@NonNull Data data) {
        super(data);
        this.rowId = data.getRowId();
        this.columnId = data.getColumnId();
        this.remoteColumnId = data.getRemoteColumnId();
        this.value = new Value(data.getValue());
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
        return rowId == data.rowId && columnId == data.columnId && remoteColumnId == data.remoteColumnId && Objects.equals(value, data.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rowId, columnId, remoteColumnId, value);
    }
}
