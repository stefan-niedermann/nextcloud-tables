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
    private final EDataType type;

    public DataFormatter(@NonNull EDataType type) {
        this.type = type;
    }

    @Nullable
    public String serializeValue(@Nullable Object rawValue) {
        if (rawValue == null) {
            return null;
        }

        final var value = String.valueOf(rawValue);

        switch (type) {
            case DATETIME:
            case DATETIME_DATETIME:
                return TextUtils.isEmpty(value) ? null : TablesAPI.FORMATTER_DATA_DATE_TIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value));
            case DATETIME_DATE:
                return TextUtils.isEmpty(value) ? null : TablesAPI.FORMATTER_DATA_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE.parse(value));
            case DATETIME_TIME:
                return TextUtils.isEmpty(value) ? null : TablesAPI.FORMATTER_DATA_TIME.format(DateTimeFormatter.ISO_LOCAL_TIME.parse(value));
            default:
                return value;
        }
    }

    @Nullable
    public String deserializeValue(@Nullable Object rawValue) {
        if (rawValue == null) {
            return null;
        }

        final var value = String.valueOf(rawValue);

        switch (type) {
            case DATETIME:
            case DATETIME_DATETIME:
                return TextUtils.isEmpty(value) ? null : DateTimeFormatter.ISO_DATE_TIME.format(TablesAPI.FORMATTER_DATA_DATE_TIME.parse(value));
            case DATETIME_DATE:
                return TextUtils.isEmpty(value) ? null : DateTimeFormatter.ISO_DATE.format(TablesAPI.FORMATTER_DATA_DATE.parse(value));
            case DATETIME_TIME:
                return TextUtils.isEmpty(value) ? null : DateTimeFormatter.ISO_TIME.format(TablesAPI.FORMATTER_DATA_TIME.parse(value));
            case SELECTION:
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
            case SELECTION_MULTI: {
                final var numbers = value
                        .replace("[", "")
                        .replace("]", "");

                if (numbers.isBlank()) {
                    return null;
                }


                final var optionIds = Arrays.stream(numbers.split(",")).map(Double::parseDouble).map(Double::longValue).collect(Collectors.toUnmodifiableSet());

                return "[" + optionIds
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","))
                        + "]";
            }
            default:
                return value;
        }
    }
}