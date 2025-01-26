package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.DBStatus;

public class DBStatusConverter {

    @TypeConverter
    public static DBStatus dbStatusFromString(@Nullable String value) {
        for (final var status : DBStatus.values()) {
            if (Objects.equals(status.title, value)) {
                return status;
            }
        }

        return DBStatus.VOID;
    }

    @TypeConverter
    public static String dbStatusToString(@Nullable DBStatus status) {
        return status == null ? null : status.title;
    }

}
