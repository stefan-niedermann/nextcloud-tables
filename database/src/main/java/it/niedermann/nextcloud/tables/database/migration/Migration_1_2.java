package it.niedermann.nextcloud.tables.database.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/435">Implement ETags for Capabilities endpoint</a>
 */
public class Migration_1_2 extends Migration {

    public Migration_1_2() {
        super(1, 2);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `Column` ADD `usergroupMultipleItems` INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE `Column` ADD `usergroupSelectUsers` INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE `Column` ADD `usergroupSelectGroups` INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE `Column` ADD `showUserStatus` INTEGER NOT NULL DEFAULT 0");
    }
}
