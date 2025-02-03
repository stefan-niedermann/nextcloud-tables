package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;


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
public record DataUserGroupCrossRef(
        long dataId,
        long userGroupId
) {
}
