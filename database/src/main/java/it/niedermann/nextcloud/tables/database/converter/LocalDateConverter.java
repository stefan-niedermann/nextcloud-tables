package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.util.Optional;

public class LocalDateConverter {

    @TypeConverter
    public static LocalDate longToLocalDate(@Nullable Long src) {
        return Optional.ofNullable(src)
                .map(LocalDate::ofEpochDay)
                .orElse(null);
    }

    @TypeConverter
    public static Long localDateToLong(@Nullable LocalDate src) {
        return Optional.ofNullable(src)
                .map(LocalDate::toEpochDay)
                .orElse(null);
    }

}
