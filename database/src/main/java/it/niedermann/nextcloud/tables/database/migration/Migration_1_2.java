package it.niedermann.nextcloud.tables.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/// Due to huge architectural changes between database version `1` and `2`, ⚠️ we delete any local data except registered accounts from the device by intention.
/// Next time the device gets connected to the internet, it will trigger a full synchronization.
/// Pro: Quite little developer efforts required
/// Contra: Not synchronized local changes will be lost
/// Given data loss was communicated clearly at this point in time in all app stores and for being able to move faster forward, this behavior by intention and  accepted.
/// This migration is based on a auto generated migration but then transformed into a manual migration to avoid referential integrity issues.
public class Migration_1_2 extends Migration {

    public Migration_1_2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `UserGroup` (`remoteId` TEXT, `key` TEXT, `type` INTEGER, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_UserGroup_accountId_remoteId` ON `UserGroup` (`accountId`, `remoteId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_UserGroup_accountId` ON `UserGroup` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `DataSelectionOptionCrossRef` (`dataId` INTEGER NOT NULL, `selectionOptionId` INTEGER NOT NULL, PRIMARY KEY(`dataId`, `selectionOptionId`), FOREIGN KEY(`dataId`) REFERENCES `Data`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`selectionOptionId`) REFERENCES `SelectionOption`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DataSelectionOptionCrossRef_dataId` ON `DataSelectionOptionCrossRef` (`dataId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DataSelectionOptionCrossRef_selectionOptionId` ON `DataSelectionOptionCrossRef` (`selectionOptionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `DataUserGroupCrossRef` (`dataId` INTEGER NOT NULL, `userGroupId` INTEGER NOT NULL, PRIMARY KEY(`dataId`, `userGroupId`), FOREIGN KEY(`dataId`) REFERENCES `Data`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`userGroupId`) REFERENCES `UserGroup`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DataUserGroupCrossRef_dataId` ON `DataUserGroupCrossRef` (`dataId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DataUserGroupCrossRef_userGroupId` ON `DataUserGroupCrossRef` (`userGroupId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `DefaultValueSelectionOptionCrossRef` (`columnId` INTEGER NOT NULL, `selectionOptionId` INTEGER NOT NULL, PRIMARY KEY(`columnId`, `selectionOptionId`), FOREIGN KEY(`columnId`) REFERENCES `Data`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`selectionOptionId`) REFERENCES `SelectionOption`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DefaultValueSelectionOptionCrossRef_columnId` ON `DefaultValueSelectionOptionCrossRef` (`columnId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DefaultValueSelectionOptionCrossRef_selectionOptionId` ON `DefaultValueSelectionOptionCrossRef` (`selectionOptionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `DefaultValueUserGroupCrossRef` (`columnId` INTEGER NOT NULL, `userGroupId` INTEGER NOT NULL, PRIMARY KEY(`columnId`, `userGroupId`), FOREIGN KEY(`columnId`) REFERENCES `Data`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`userGroupId`) REFERENCES `UserGroup`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DefaultValueUserGroupCrossRef_columnId` ON `DefaultValueUserGroupCrossRef` (`columnId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_DefaultValueUserGroupCrossRef_userGroupId` ON `DefaultValueUserGroupCrossRef` (`userGroupId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `SearchProvider` (`remoteId` TEXT NOT NULL, `appId` TEXT, `name` TEXT, `icon` TEXT, `order` INTEGER NOT NULL, `inAppSearch` INTEGER NOT NULL, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_SearchProvider_accountId_remoteId` ON `SearchProvider` (`accountId`, `remoteId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_SearchProvider_accountId` ON `SearchProvider` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `TextAllowedPattern` (`pattern` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `LinkValue` (`dataId` INTEGER NOT NULL, `providerId` INTEGER, `title` TEXT, `subline` TEXT, `value` TEXT NOT NULL, PRIMARY KEY(`dataId`), FOREIGN KEY(`dataId`) REFERENCES `Data`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`providerId`) REFERENCES `SearchProvider`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_LinkValue_providerId` ON `LinkValue` (`providerId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_LinkValue_dataId` ON `LinkValue` (`dataId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_LinkValue_dataId_providerId` ON `LinkValue` (`dataId`, `providerId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_Account` (`url` TEXT NOT NULL DEFAULT '', `userName` TEXT NOT NULL DEFAULT '', `accountName` TEXT NOT NULL DEFAULT '', `nextcloudVersion` TEXT, `tablesVersion` TEXT, `color` INTEGER NOT NULL DEFAULT -16743735, `displayName` TEXT, `currentTable` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `user_status` TEXT, `user_eTag` TEXT, `capabilities_status` TEXT, `capabilities_eTag` TEXT, `search_status` TEXT, `search_eTag` TEXT, FOREIGN KEY(`currentTable`) REFERENCES `Table`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("INSERT INTO `_new_Account` (`url`,`userName`,`accountName`,`nextcloudVersion`,`tablesVersion`,`color`,`displayName`,`id`) SELECT `url`,`userName`,`accountName`,`nextcloudVersion`,`tablesVersion`,`color`,`displayName`,`id` FROM `Account`");
        db.execSQL("DROP TABLE `Account`");
        db.execSQL("ALTER TABLE `_new_Account` RENAME TO `Account`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Account_user_status` ON `Account` (`user_status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Account_capabilities_status` ON `Account` (`capabilities_status`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Account_userName_url` ON `Account` (`userName`, `url`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Account_accountName` ON `Account` (`accountName`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Account_currentTable` ON `Account` (`currentTable`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_Table` (`title` TEXT NOT NULL DEFAULT '', `description` TEXT DEFAULT '', `emoji` TEXT DEFAULT '', `ownership` TEXT DEFAULT '', `ownerDisplayName` TEXT DEFAULT '', `createdBy` TEXT DEFAULT '', `createdAt` INTEGER, `lastEditBy` TEXT DEFAULT '', `lastEditAt` INTEGER, `isShared` INTEGER NOT NULL, `remoteId` INTEGER, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `read` INTEGER NOT NULL, `create` INTEGER NOT NULL, `update` INTEGER NOT NULL, `delete` INTEGER NOT NULL, `manage` INTEGER NOT NULL, `status` TEXT, `eTag` TEXT)");
        db.execSQL("DROP TABLE `Table`");
        db.execSQL("ALTER TABLE `_new_Table` RENAME TO `Table`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_title` ON `Table` (`title`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_isShared` ON `Table` (`isShared`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_manage` ON `Table` (`manage`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_read` ON `Table` (`read`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_status` ON `Table` (`status`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Table_remoteId_accountId` ON `Table` (`remoteId`, `accountId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Table_accountId` ON `Table` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_Column` (`title` TEXT DEFAULT '', `createdBy` TEXT DEFAULT '', `createdAt` INTEGER, `lastEditBy` TEXT DEFAULT '', `lastEditAt` INTEGER, `dataType` INTEGER NOT NULL DEFAULT 0, `mandatory` INTEGER NOT NULL, `description` TEXT DEFAULT '', `orderWeight` INTEGER, `tableId` INTEGER NOT NULL, `remoteId` INTEGER, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `default_stringValue` TEXT, `default_booleanValue` INTEGER, `default_doubleValue` REAL, `default_instantValue` INTEGER, `default_dateValue` INTEGER, `default_timeValue` INTEGER, `default_linkValueRef` INTEGER, `numberMin` REAL, `numberMax` REAL, `numberDecimals` INTEGER, `numberPrefix` TEXT, `numberSuffix` TEXT, `textAllowedPattern` TEXT, `textMaxLength` INTEGER, `usergroupMultipleItems` INTEGER NOT NULL, `usergroupSelectUsers` INTEGER NOT NULL, `usergroupSelectGroups` INTEGER NOT NULL, `showUserStatus` INTEGER NOT NULL, `status` TEXT, `eTag` TEXT)");
        db.execSQL("DROP TABLE `Column`");
        db.execSQL("ALTER TABLE `_new_Column` RENAME TO `Column`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Column_orderWeight` ON `Column` (`orderWeight`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Column_tableId` ON `Column` (`tableId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Column_status` ON `Column` (`status`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Column_remoteId_accountId` ON `Column` (`remoteId`, `accountId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Column_accountId` ON `Column` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_Row` (`createdBy` TEXT DEFAULT '', `createdAt` INTEGER, `lastEditBy` TEXT DEFAULT '', `lastEditAt` INTEGER, `tableId` INTEGER NOT NULL, `remoteId` INTEGER, `accountId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `status` TEXT, `eTag` TEXT)");
        db.execSQL("DROP TABLE `Row`");
        db.execSQL("ALTER TABLE `_new_Row` RENAME TO `Row`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Row_tableId` ON `Row` (`tableId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Row_status` ON `Row` (`status`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Row_remoteId_accountId` ON `Row` (`remoteId`, `accountId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Row_accountId` ON `Row` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_Data` (`rowId` INTEGER NOT NULL, `columnId` INTEGER NOT NULL, `remoteColumnId` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `stringValue` TEXT, `booleanValue` INTEGER, `doubleValue` REAL, `instantValue` INTEGER, `dateValue` INTEGER, `timeValue` INTEGER, `linkValueRef` INTEGER, FOREIGN KEY(`rowId`) REFERENCES `Row`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`columnId`) REFERENCES `Column`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`linkValueRef`) REFERENCES `LinkValue`(`dataId`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("DROP TABLE `Data`");
        db.execSQL("ALTER TABLE `_new_Data` RENAME TO `Data`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Data_columnId` ON `Data` (`columnId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Data_linkValueRef` ON `Data` (`linkValueRef`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Data_rowId_columnId` ON `Data` (`rowId`, `columnId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Data_rowId_remoteColumnId` ON `Data` (`rowId`, `remoteColumnId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `_new_SelectionOption` (`remoteId` INTEGER, `columnId` INTEGER NOT NULL, `label` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`columnId`) REFERENCES `Column`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("DROP TABLE `SelectionOption`");
        db.execSQL("ALTER TABLE `_new_SelectionOption` RENAME TO `SelectionOption`");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_SelectionOption_remoteId` ON `SelectionOption` (`remoteId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_SelectionOption_columnId_remoteId` ON `SelectionOption` (`columnId`, `remoteId`)");
    }
}
