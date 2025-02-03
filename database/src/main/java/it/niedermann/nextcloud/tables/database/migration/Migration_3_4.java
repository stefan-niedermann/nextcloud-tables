package it.niedermann.nextcloud.tables.database.migration;

import androidx.room.DeleteColumn;
import androidx.room.migration.AutoMigrationSpec;

@DeleteColumn.Entries(
        @DeleteColumn(
                tableName = "UserGroup",
                columnName = "key"
        )
)
public class Migration_3_4 implements AutoMigrationSpec {
}
