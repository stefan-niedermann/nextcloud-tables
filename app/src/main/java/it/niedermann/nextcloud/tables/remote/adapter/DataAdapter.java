package it.niedermann.nextcloud.tables.remote.adapter;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.niedermann.nextcloud.tables.TablesApplication.FeatureToggle;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.model.EDataType;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import it.niedermann.nextcloud.tables.remote.util.TablesSerializationUtil;

public class DataAdapter {

    private static final String TAG = DataAdapter.class.getSimpleName();
    private final TablesSerializationUtil util;


    public DataAdapter() {
        this(new TablesSerializationUtil());
    }

    public DataAdapter(@NonNull TablesSerializationUtil util) {
        this.util = util;
    }

    /**
     * @see TablesAPI#createRow(long, JsonElement)
     */
    @NonNull
    public JsonElement serialize(@NonNull List<Column> columns, @NonNull Data[] dataset) {
        final var properties = new JsonObject();

        for (final var data : dataset) {
            properties.add(String.valueOf(data.getRemoteColumnId()), serialize(getTypeForData(columns, data), data));
        }

        return properties;
    }

    /**
     * @return {@link JsonElement} representing the {@link Data#getValue()}
     */
    @NonNull
    public JsonElement serialize(@NonNull EDataType type, @NonNull Data data) {
        final var value = data.getValue();

        if (TextUtils.isEmpty(value)) {
            return JsonNull.INSTANCE;
        }

        switch (type) {
            case DATETIME:
            case DATETIME_DATETIME: {
                return new JsonPrimitive(TablesAPI.FORMATTER_DATA_DATE_TIME.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value)));
            }
            case DATETIME_DATE: {
                return new JsonPrimitive(TablesAPI.FORMATTER_DATA_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE.parse(value)));
            }
            case DATETIME_TIME: {
                return new JsonPrimitive(TablesAPI.FORMATTER_DATA_TIME.format(DateTimeFormatter.ISO_LOCAL_TIME.parse(value)));
            }
            case SELECTION_MULTI: {
                final var jsonArray = new JsonArray();
                Arrays.stream(value.split(",")).forEach(jsonArray::add);
                return jsonArray;
            }
            default:
                return new JsonPrimitive(value);
        }
    }

    @NonNull
    public Data[] deserialize(@NonNull List<Column> columns, @NonNull Data[] dataset) {
        final var result = new ArrayList<Data>(dataset.length);

        for (final var data : dataset) {
            result.add(deserialize(getTypeForData(columns, data), data));
        }

        return result.toArray(Data[]::new);
    }

    @NonNull
    public Data deserialize(@NonNull EDataType type, @NonNull Data originalData) {
        final var data = new Data(originalData);
        data.setValue(deserialize(type, data.getValue()));
        return originalData;
    }

    @Nullable
    public String deserialize(@NonNull EDataType type, @Nullable String value) {
        if (TextUtils.isEmpty(value)) {
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
                return value.isBlank() ? null : String.valueOf(Long.parseLong(value.trim()));
            case SELECTION_MULTI:
                return util.deserializeArray(value);
            default:
                return value;
        }
    }

    public EDataType getTypeForData(@NonNull List<Column> columns, @NonNull Data data) {
        for (final var column : columns) {
            if (column.getId() == data.getColumnId()) {
                return EDataType.findByColumn(column);
            }
        }

        if (FeatureToggle.STRICT_MODE.enabled) {
            throw new IllegalStateException("Failed to find column for " + data + " (remoteColumnId: " + data.getRemoteColumnId() + ")");
        }

        return EDataType.UNKNOWN;
    }
}