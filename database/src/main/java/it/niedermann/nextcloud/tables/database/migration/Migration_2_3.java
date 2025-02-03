package it.niedermann.nextcloud.tables.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/// Fixes not enforced Foreign Key constraints:
/// [androidx.room.ForeignKey] was used in abstract super classes of [androidx.room.Entity] but not inherited as expected..
/// This was fixed in version `2.0.3`, but it might be possible that there are already constraint violations in the wild.
/// ⚠️ We therefore delete all data except the accounts. Synchronization should start after first app start and fetch the data again.
public class Migration_2_3 extends Migration {

    public Migration_2_3() {
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        // Clear database except accounts
        db.execSQL("DELETE FROM `Table`");
        db.execSQL("DELETE FROM `Column`");
        db.execSQL("DELETE FROM `Row`");
        db.execSQL("DELETE FROM Data");
        db.execSQL("DELETE FROM SelectionOption");
        db.execSQL("DELETE FROM UserGroup");
        db.execSQL("DELETE FROM DataSelectionOptionCrossRef");
        db.execSQL("DELETE FROM DataUserGroupCrossRef");
        db.execSQL("DELETE FROM DefaultValueSelectionOptionCrossRef");
        db.execSQL("DELETE FROM DefaultValueUserGroupCrossRef");
        db.execSQL("DELETE FROM SearchProvider");
        db.execSQL("DELETE FROM TextAllowedPattern");
        db.execSQL("DELETE FROM LinkValue");

        // Clear currentTable as they lack integrity Foreign Key preserving other data than (not yet existing) Foreign Keys
        db.execSQL("UPDATE `Account` SET currentTable = NULL");

        // Recreate entities with Foreign Key references without preserving data
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_Table` (`title` TEXT NOT NULL DEFAULT '', `description` TEXT DEFAULT '', `emoji` TEXT DEFAULT '', `ownership` TEXT DEFAULT '', `ownerDisplayName` TEXT DEFAULT '', `createdBy` TEXT DEFAULT '', `createdAt` INTEGER, `lastEditBy` TEXT DEFAULT '', `lastEditAt` INTEGER, `isShared` INTEGER NOT NULL, `currentRow` INTEGER, `remoteId` INTEGER, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `read` INTEGER NOT NULL, `create` INTEGER NOT NULL, `update` INTEGER NOT NULL, `delete` INTEGER NOT NULL, `manage` INTEGER NOT NULL, `status` TEXT, `eTag` TEXT, FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`currentRow`) REFERENCES `Row`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("DROP TABLE `Table`");
        db.execSQL("ALTER TABLE `_new_Table` RENAME TO `Table`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_title` ON `Table` (`title`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_isShared` ON `Table` (`isShared`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_manage` ON `Table` (`manage`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_read` ON `Table` (`read`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_currentRow` ON `Table` (`currentRow`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_status` ON `Table` (`status`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Table_remoteId_accountId` ON `Table` (`remoteId`, `accountId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_accountId` ON `Table` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_Column` (`title` TEXT DEFAULT '', `createdBy` TEXT DEFAULT '', `createdAt` INTEGER, `lastEditBy` TEXT DEFAULT '', `lastEditAt` INTEGER, `dataType` INTEGER NOT NULL DEFAULT 0, `mandatory` INTEGER NOT NULL, `description` TEXT DEFAULT '', `orderWeight` INTEGER, `tableId` INTEGER NOT NULL, `remoteId` INTEGER, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `default_stringValue` TEXT, `default_booleanValue` INTEGER, `default_doubleValue` REAL, `default_instantValue` INTEGER, `default_dateValue` INTEGER, `default_timeValue` INTEGER, `default_linkValueRef` INTEGER, `numberMin` REAL, `numberMax` REAL, `numberDecimals` INTEGER, `numberPrefix` TEXT, `numberSuffix` TEXT, `textAllowedPattern` TEXT, `textMaxLength` INTEGER, `usergroupMultipleItems` INTEGER NOT NULL, `usergroupSelectUsers` INTEGER NOT NULL, `usergroupSelectGroups` INTEGER NOT NULL, `showUserStatus` INTEGER NOT NULL, `status` TEXT, `eTag` TEXT, FOREIGN KEY(`tableId`) REFERENCES `Table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("DROP TABLE `Column`");
        db.execSQL("ALTER TABLE `_new_Column` RENAME TO `Column`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Column_orderWeight` ON `Column` (`orderWeight`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Column_tableId` ON `Column` (`tableId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Column_status` ON `Column` (`status`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Column_remoteId_accountId` ON `Column` (`remoteId`, `accountId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Column_accountId` ON `Column` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_Row` (`createdBy` TEXT DEFAULT '', `createdAt` INTEGER, `lastEditBy` TEXT DEFAULT '', `lastEditAt` INTEGER, `tableId` INTEGER NOT NULL, `remoteId` INTEGER, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `status` TEXT, `eTag` TEXT, FOREIGN KEY(`tableId`) REFERENCES `Table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("DROP TABLE `Row`");
        db.execSQL("ALTER TABLE `_new_Row` RENAME TO `Row`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Row_tableId` ON `Row` (`tableId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Row_status` ON `Row` (`status`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Row_remoteId_accountId` ON `Row` (`remoteId`, `accountId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Row_accountId` ON `Row` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_UserGroup` (`remoteId` TEXT, `key` TEXT, `type` INTEGER, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("DROP TABLE `UserGroup`");
        db.execSQL("ALTER TABLE `_new_UserGroup` RENAME TO `UserGroup`");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_UserGroup_accountId_remoteId` ON `UserGroup` (`accountId`, `remoteId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_UserGroup_accountId` ON `UserGroup` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_SearchProvider` (`remoteId` TEXT NOT NULL, `appId` TEXT, `name` TEXT, `icon` TEXT, `order` INTEGER NOT NULL, `inAppSearch` INTEGER NOT NULL, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("DROP TABLE `SearchProvider`");
        db.execSQL("ALTER TABLE `_new_SearchProvider` RENAME TO `SearchProvider`");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_SearchProvider_accountId_remoteId` ON `SearchProvider` (`accountId`, `remoteId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_SearchProvider_accountId` ON `SearchProvider` (`accountId`)");
    }
}
