package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.Instant;
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
                        entity = Table.class,
                        parentColumns = "id",
                        childColumns = "tableId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(name = "IDX_ROW_ACCOUNT_ID_REMOTE_ID", value = {"accountId", "remoteId"}, unique = true),
                @Index(name = "IDX_ROW_TABLE_ID", value = "tableId")
        }
)
public class Row extends AbstractRemoteEntity {
    @SerializedName("localTableId")
    @Expose(deserialize = false, serialize = false)
    private long tableId;
    @SerializedName("tableId")
    private long tableRemoteId;
    @ColumnInfo(defaultValue = "")
    private String createdBy;
    private Instant createdAt;
    @ColumnInfo(defaultValue = "")
    private String lastEditBy;
    private Instant lastEditAt;
    @Ignore
    private Data[] data;

    public Row() {
        // Default constructor
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public long getTableRemoteId() {
        return tableRemoteId;
    }

    public void setTableRemoteId(long tableRemoteId) {
        this.tableRemoteId = tableRemoteId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastEditBy() {
        return lastEditBy;
    }

    public void setLastEditBy(String lastEditBy) {
        this.lastEditBy = lastEditBy;
    }

    public Instant getLastEditAt() {
        return lastEditAt;
    }

    public void setLastEditAt(Instant lastEditAt) {
        this.lastEditAt = lastEditAt;
    }

    @Ignore
    public Data[] getData() {
        return data;
    }

    @Ignore
    public void setData(Data[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Row row = (Row) o;
        return tableId == row.tableId && tableRemoteId == row.tableRemoteId && Objects.equals(createdBy, row.createdBy) && Objects.equals(createdAt, row.createdAt) && Objects.equals(lastEditBy, row.lastEditBy) && Objects.equals(lastEditAt, row.lastEditAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tableId, tableRemoteId, createdBy, createdAt, lastEditBy, lastEditAt);
    }
}
