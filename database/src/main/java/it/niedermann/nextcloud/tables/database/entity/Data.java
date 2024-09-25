package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;


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
                ),
                @ForeignKey(
                        entity = Row.class,
                        parentColumns = "id",
                        childColumns = "rowId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(name = "IDX_DATA_ACCOUNT_ID_REMOTE_COLUMN_ID", value = {"accountId", "remoteColumnId"}),
                @Index(name = "IDX_DATA_COLUMN_ID_ROW_ID", value = {"columnId", "rowId"}, unique = true),
                @Index(name = "IDX_DATA_COLUMN_ID", value = "columnId"),
                @Index(name = "IDX_DATA_ROW_ID", value = "rowId")
        }
)
public class Data extends AbstractAccountRelatedEntity {

    @SerializedName("localColumnId")
    @Expose(deserialize = false, serialize = false)
    private long columnId;
    @SerializedName("localRowId")
    @Expose(deserialize = false, serialize = false)
    private long rowId;
    @Nullable
    @SerializedName("columnId")
    private Long remoteColumnId;
    @Nullable
    private JsonElement value;

    public Data() {
        // Default constructor
    }

    @Ignore
    public Data(@NonNull Data data) {
        setId(data.getId());
        setAccountId(data.getAccountId());
        setRowId(data.getRowId());
        setColumnId(data.getColumnId());
        setRemoteColumnId(data.getRemoteColumnId());
        setETag(data.getETag());
        setStatus(data.getStatus());
        setValue(data.getValue());
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    @Nullable
    public Long getRemoteColumnId() {
        return remoteColumnId;
    }

    public void setRemoteColumnId(@Nullable Long remoteColumnId) {
        this.remoteColumnId = remoteColumnId;
    }

    @Nullable
    public JsonElement getValue() {
        return value;
    }

    public void setValue(@Nullable JsonElement value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Data data = (Data) o;
        return columnId == data.columnId && rowId == data.rowId && Objects.equals(remoteColumnId, data.remoteColumnId) && Objects.equals(value, data.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), columnId, rowId, remoteColumnId, value);
    }

    @NonNull
    @Override
    public String toString() {
        return getValue() + " (row: " + getRowId() + ", column: " + getColumnId() + ")";
    }
}
