package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Objects;


@Entity(
        primaryKeys = {"dataId", "userGroupId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Data.class,
                        parentColumns = "id",
                        childColumns = "dataId",
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
                @Index(value = {"dataId"}),
                @Index(value = {"userGroupId"})
        }
)
public class DataUserGroupCrossRef {

    public long dataId;
    public long userGroupId;

    public DataUserGroupCrossRef() {
        // Default constructor
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataUserGroupCrossRef that = (DataUserGroupCrossRef) o;
        return dataId == that.dataId && userGroupId == that.userGroupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataId, userGroupId);
    }
}
