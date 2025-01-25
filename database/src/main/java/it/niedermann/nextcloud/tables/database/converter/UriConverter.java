package it.niedermann.nextcloud.tables.database.converter;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.util.Optional;

public class UriConverter {

    @TypeConverter
    public static Uri dbStatusFromString(@Nullable String link) {
        return Uri.parse(link);
    }

    @TypeConverter
    public static String dbStatusToString(@Nullable Uri link) {
        return Optional
                .ofNullable(link)
                .map(Uri::toString)
                .orElse(null);
    }

}
