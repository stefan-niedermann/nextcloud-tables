package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.LocalTime;
import java.util.Optional;

public class LocalTimeConverter {

    @TypeConverter
    public static LocalTime longToLocalTime(@Nullable Integer src) {
        return Optional.ofNullable(src)
                .map(LocalTime::ofSecondOfDay)
                .orElse(null);
    }

    @TypeConverter
    public static Integer instantToInteger(@Nullable LocalTime src) {
        return Optional.ofNullable(src)
                .map(LocalTime::toSecondOfDay)
                .orElse(null);
    }

}
