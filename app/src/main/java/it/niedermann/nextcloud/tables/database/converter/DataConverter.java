package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

public class DataConverter {

    @TypeConverter
    public static Object objectFromString(@Nullable String o) {
        return o;
    }

    @TypeConverter
    public static String objectToString(@Nullable Object o) {
        return o == null ? "" : o.toString();
    }

}
