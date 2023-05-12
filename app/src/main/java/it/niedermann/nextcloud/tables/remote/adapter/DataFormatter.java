package it.niedermann.nextcloud.tables.remote.adapter;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.model.types.EDataType;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;

public class DataFormatter {

    private static final String TAG = DataFormatter.class.getSimpleName();

    @Nullable
    public String serializeValue(@NonNull EDataType type, @Nullable String value) {
        if (value == null) {
            return null;
        }

        switch (type) {
            case DATETIME:
            case DATETIME_DATETIME:
                return TextUtils.isEmpty(value) ? null : TablesAPI.FORMATTER_DATA_DATE_TIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value));
            case DATETIME_DATE:
                return TextUtils.isEmpty(value) ? null : TablesAPI.FORMATTER_DATA_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE.parse(value));
            case DATETIME_TIME:
                return TextUtils.isEmpty(value) ? null : TablesAPI.FORMATTER_DATA_TIME.format(DateTimeFormatter.ISO_LOCAL_TIME.parse(value));
            case SELECTION_MULTI:
                return TextUtils.isEmpty(value) ? "[]" : "[" + value + "]";
            default:
                return value;
        }
    }

    @Nullable
    public String deserializeValue(@NonNull EDataType type, @Nullable String value) {
        if (value == null) {
            return null;
        }

        switch (type) {
            case DATETIME:
            case DATETIME_DATETIME:
                return TextUtils.isEmpty(value) ? null : DateTimeFormatter.ISO_DATE_TIME.format(TablesAPI.FORMATTER_DATA_DATE_TIME.parse(value));
            case DATETIME_DATE:
                return TextUtils.isEmpty(value) ? null : DateTimeFormatter.ISO_DATE.format(TablesAPI.FORMATTER_DATA_DATE.parse(value));
            case DATETIME_TIME:
                return TextUtils.isEmpty(value) ? null : DateTimeFormatter.ISO_TIME.format(TablesAPI.FORMATTER_DATA_TIME.parse(value));
            case NUMBER:
            case NUMBER_PROGRESS:
            case NUMBER_STARS: {
                try {
                    return String.valueOf(Long.parseLong(value));
                } catch (NumberFormatException noInteger) {
                    try {
                        return String.valueOf(Double.parseDouble(value));
                    } catch (NumberFormatException noDouble) {
                        Log.w(TAG, "Expected type to be Long or Double: " + value);
                        return value;
                    }
                }
            }
            case SELECTION:
                return value.isBlank() ? null : String.valueOf((long) Double.parseDouble(value));
            case SELECTION_MULTI: {
                final var valueWithoutJsonArrayBrackets = value.replace("[", "").replace("]", "");

                if (valueWithoutJsonArrayBrackets.isBlank()) {
                    return null;
                }

                return Arrays.stream(valueWithoutJsonArrayBrackets.replace("[", "").replace("]", "").split(","))
                        .map(Double::parseDouble)
                        .map(Double::longValue)
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
            }
            default:
                return value;
        }
    }
}