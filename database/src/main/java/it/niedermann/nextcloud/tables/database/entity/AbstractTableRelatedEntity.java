package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.Objects;

@Entity(
        inheritSuperIndices = true,
        indices = {
                @Index(value = "tableId"),
        }
)
public class AbstractTableRelatedEntity extends AbstractRemoteEntity {

    private long tableId;

    public AbstractTableRelatedEntity() {
        // Default constructor
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    @NonNull
    @Override
    public String toString() {
        return "AbstractTableRelatedEntity{" +
                "tableId=" + tableId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractTableRelatedEntity that = (AbstractTableRelatedEntity) o;
        return tableId == that.tableId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tableId);
    }
}
