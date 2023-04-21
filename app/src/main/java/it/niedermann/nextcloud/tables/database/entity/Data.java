package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Column.class,
                        parentColumns = {"accountId", "remoteId"},
                        childColumns = {"accountId", "remoteColumnId"},
                        onDelete = ForeignKey.CASCADE
                )
        },
//        indices = {
//                @Index(name = "IDX_DATA_COLUMN_ID", value = "columnId"),
//                @Index(name = "IDX_DATA_ROW_ID", value = "rowId")
//        },
        primaryKeys = {"accountId", "remoteColumnId"}
)
public class Data extends AbstractRemoteEntity {

//    @Expose(deserialize = false, serialize = false)
//    private long accountId;
//    @SerializedName("localColumnId")
//    @Expose(deserialize = false, serialize = false)
//    private long columnId;
//    @SerializedName("localColumnId")
//    @Expose(deserialize = false, serialize = false)
//    private long rowId;
    @SerializedName("columnId")
    private long remoteColumnId;
    private Object value;

    public Data() {
        // Default constructor
    }

//    public long getAccountId() {
//        return accountId;
//    }c
//
//    public void setAccountId(long accountId) {
//        this.accountId = accountId;
//    }
//
//    public long getColumnId() {
//        return columnId;
//    }
//
//    public void setColumnId(long columnId) {
//        this.columnId = columnId;
//    }
//
//    public long getRowId() {
//        return rowId;
//    }
//
//    public void setRowId(long rowId) {
//        this.rowId = rowId;
//    }

    public long getRemoteColumnId() {
        return remoteColumnId;
    }

    public void setRemoteColumnId(long remoteColumnId) {
        this.remoteColumnId = remoteColumnId;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Data data = (Data) o;
//        return accountId == data.accountId && columnId == data.columnId && rowId == data.rowId && remoteColumnId == data.remoteColumnId && Objects.equals(value, data.value);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(accountId, columnId, rowId, remoteColumnId, value);
//    }
}
