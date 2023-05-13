package it.niedermann.nextcloud.tables.remote.adapter;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.tables.model.types.EDataType;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import it.niedermann.nextcloud.tables.remote.util.TablesSerializationUtil;

/**
 * Handles value formatting differences between remote and local database
 */
public class DataAdapter {

    private static final String TAG = DataAdapter.class.getSimpleName();
    private final TablesSerializationUtil util;

    public DataAdapter() {
        this(new TablesSerializationUtil());
    }

    public DataAdapter(@NonNull TablesSerializationUtil util) {
        this.util = util;
    }

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
                return util.serializeArray(value);
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
            case SELECTION_MULTI:
                return util.deserializeArray(value);
            default:
                return value;
        }
    }
}