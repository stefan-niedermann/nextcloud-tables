package it.niedermann.nextcloud.tables.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;

/// - Adds `usergroupSelectTeams` flag to `Column` entity
///
/// The following steps are "breaking changes" because users will lose local not yet synchronized changes
/// - Changes the relation of `DefaultValueSelectionOptionCrossRef` from `Data` to `Column`
/// - Changes the relation of `DefaultValueUserGroupCrossRef` from `Data` to `Column`
public class Migration_5_6 extends Migration {

    public Migration_5_6() {
        super(5, 6);
    }

    @Override
    public void migrate(@NonNull final SQLiteConnection connection) {
        addUserGroupSelectTeamsToColumn(connection);
        replaceDefaultValueSelectionOptionCrossRefForeignKey(connection);
        replaceDefaultValueUserGroupCrossRefForeignKey(connection);
    }

    private void addUserGroupSelectTeamsToColumn(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, """
                ALTER TABLE `Column`
                ADD `usergroupSelectTeams` INTEGER NOT NULL DEFAULT 0
                """);
    }

    private void replaceDefaultValueSelectionOptionCrossRefForeignKey(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, """
                DROP TABLE `DefaultValueSelectionOptionCrossRef`;
                """);

        SQLite.execSQL(connection, """
                CREATE TABLE `DefaultValueSelectionOptionCrossRef` (
                    `columnId` INTEGER NOT NULL,
                    `selectionOptionId` INTEGER NOT NULL,
                    PRIMARY KEY(`columnId`, `selectionOptionId`),
                    FOREIGN KEY(`columnId`) REFERENCES `Column`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE ,
                    FOREIGN KEY(`selectionOptionId`) REFERENCES `SelectionOption`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                );
                """);

        SQLite.execSQL(connection, """
                CREATE INDEX `index_DefaultValueSelectionOptionCrossRef_columnId`
                ON `DefaultValueSelectionOptionCrossRef` (`columnId`);
                """);

        SQLite.execSQL(connection, """
                CREATE INDEX `index_DefaultValueSelectionOptionCrossRef_selectionOptionId`
                ON `DefaultValueSelectionOptionCrossRef` (`selectionOptionId`);
                """);
    }

    private void replaceDefaultValueUserGroupCrossRefForeignKey(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, """
                DROP TABLE `DefaultValueUserGroupCrossRef`;
                """);

        SQLite.execSQL(connection, """
                CREATE TABLE `DefaultValueUserGroupCrossRef` (
                    `columnId` INTEGER NOT NULL,
                    `userGroupId` INTEGER NOT NULL,
                    PRIMARY KEY(`columnId`, `userGroupId`),
                    FOREIGN KEY(`columnId`) REFERENCES `Column`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`userGroupId`) REFERENCES `UserGroup`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                );
                """);

        SQLite.execSQL(connection, """
                CREATE INDEX `index_DefaultValueUserGroupCrossRef_columnId`
                ON `DefaultValueUserGroupCrossRef` (`columnId`);
                """);

        SQLite.execSQL(connection, """
                CREATE INDEX `index_DefaultValueUserGroupCrossRef_userGroupId`
                ON `DefaultValueUserGroupCrossRef` (`userGroupId`);
                """);
    }
}
