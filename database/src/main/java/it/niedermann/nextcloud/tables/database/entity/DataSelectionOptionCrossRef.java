package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        primaryKeys = {"dataId", "selectionOptionId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Data.class,
                        parentColumns = "id",
                        childColumns = "dataId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = SelectionOption.class,
                        parentColumns = "id",
                        childColumns = "selectionOptionId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"dataId"}),
                @Index(value = {"selectionOptionId"})
        }
)
public record DataSelectionOptionCrossRef(
        long dataId,
        long selectionOptionId
) {
}

