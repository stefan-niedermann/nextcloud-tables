package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

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
                @Index(name = "IDX_ROW_TABLE_ID_REMOTE_ID", value = {"tableId", "remoteId"}, unique = true),
                @Index(name = "IDX_ROW_TABLE_ID", value = "tableId")
        }
)
public class Row extends AbstractRemoteEntity {

    private long tableId;

    @ColumnInfo(defaultValue = "")
    private String createdBy;

    private Instant createdAt;

    @ColumnInfo(defaultValue = "")
    private String lastEditBy;

    private Instant lastEditAt;

    public Row() {
        // Default constructor
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
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

    @NonNull
    @Override
    public String toString() {
        return "Row{" +
                "tableId=" + tableId +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", lastEditBy='" + lastEditBy + '\'' +
                ", lastEditAt=" + lastEditAt +
                ", remoteId=" + remoteId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", eTag='" + eTag + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Row row = (Row) o;
        return tableId == row.tableId && Objects.equals(createdBy, row.createdBy) && Objects.equals(createdAt, row.createdAt) && Objects.equals(lastEditBy, row.lastEditBy) && Objects.equals(lastEditAt, row.lastEditAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tableId, createdBy, createdAt, lastEditBy, lastEditAt);
    }
}
