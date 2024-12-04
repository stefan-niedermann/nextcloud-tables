package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import it.niedermann.nextcloud.tables.database.DBStatus;

public class DBStatusConverter {

    @TypeConverter
    public static DBStatus dbStatusFromString(@Nullable String value) {
        for (DBStatus status : DBStatus.values()) {
            if (status.getTitle().equals(value)) {
                return status;
            }
        }
        return DBStatus.VOID;
    }

    @TypeConverter
    public static String dbStatusToString(@Nullable DBStatus status) {
        return status == null ? null : status.getTitle();
    }

}
