package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Objects;


@Entity(
        primaryKeys = {"columnId", "userGroupId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Data.class,
                        parentColumns = "id",
                        childColumns = "columnId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = UserGroup.class,
                        parentColumns = "id",
                        childColumns = "userGroupId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"columnId"}),
                @Index(value = {"userGroupId"})
        }
)
public class DefaultValueUserGroupCrossRef {

    public long columnId;
    public long userGroupId;

    public DefaultValueUserGroupCrossRef() {
        // Default constructor
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultValueUserGroupCrossRef that = (DefaultValueUserGroupCrossRef) o;
        return columnId == that.columnId && userGroupId == that.userGroupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnId, userGroupId);
    }
}
