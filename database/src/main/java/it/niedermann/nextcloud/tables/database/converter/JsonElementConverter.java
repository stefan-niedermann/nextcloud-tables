package it.niedermann.nextcloud.tables.database.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonElementConverter {

    @TypeConverter
    public static JsonElement jsonElementFromString(@Nullable String value) {
        if (value == null) {
            return null;
        }

        return JsonParser.parseString(value);
    }

    @TypeConverter
    public static String jsonElementToString(@Nullable JsonElement value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }
}
