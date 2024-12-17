package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.util.Optional;

public class InstantConverter {

    @TypeConverter
    public static Instant longToInstant(@Nullable Long instant) {
        return Optional.ofNullable(instant)
                .map(Instant::ofEpochMilli)
                .orElse(null);
    }

    @TypeConverter
    public static Long instantToLong(@Nullable Instant instant) {
        return Optional.ofNullable(instant)
                .map(Instant::toEpochMilli)
                .orElse(null);
    }

}
