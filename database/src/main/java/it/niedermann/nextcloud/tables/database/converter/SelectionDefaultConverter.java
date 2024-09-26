package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import com.google.gson.JsonParser;

import it.niedermann.nextcloud.tables.database.model.SelectionDefault;

public class SelectionDefaultConverter {

    @TypeConverter
    public static SelectionDefault selectionDefaultFromString(@Nullable String value) {
        return value == null ? null : new SelectionDefault(JsonParser.parseString(value));
    }

    @TypeConverter
    public static String selectionDefaultToString(@Nullable SelectionDefault value) {
        return value == null ? null : value.toString();
    }

}
