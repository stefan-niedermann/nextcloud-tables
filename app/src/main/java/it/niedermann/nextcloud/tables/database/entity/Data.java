package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

@Entity(
        inheritSuperIndices = true,
        foreignKeys = {
                @ForeignKey(
                        entity = Column.class,
                        parentColumns = "id",
                        childColumns = "columnId",
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
                @Index(name = "IDX_DATA_COLUMN_ID", value = "columnId"),
                @Index(name = "IDX_DATA_ROW_ID", value = "rowId")
        },
        primaryKeys = {"columnId", "rowId"}
)
public class Data implements Serializable {
    @SerializedName("localColumnId")
    @Expose(deserialize = false, serialize = false)
    private long columnId;
    @SerializedName("localColumnId")
    @Expose(deserialize = false, serialize = false)
    private long rowId;
    @SerializedName("columnId")
    private long remoteColumnId;
    private String value;

    public Data() {
        // Default constructor
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

    public long getRemoteColumnId() {
        return remoteColumnId;
    }

    public void setRemoteColumnId(long remoteColumnId) {
        this.remoteColumnId = remoteColumnId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return columnId == data.columnId && rowId == data.rowId && remoteColumnId == data.remoteColumnId && Objects.equals(value, data.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnId, rowId, remoteColumnId, value);
    }
}
