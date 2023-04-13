package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.Instant;

import it.niedermann.nextcloud.tables.database.DBStatus;

public class InstantConverter {

    @TypeConverter
    public static Instant longToInstant(@Nullable Long instant) {
        if (instant == null) {
            return null;
        }
        return Instant.ofEpochMilli(instant);
    }

    @TypeConverter
    public static Long instantToLong(@Nullable Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.toEpochMilli();
    }

}
