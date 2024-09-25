package it.niedermann.nextcloud.tables.types.defaults.supplier.datetime;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class TimeDefaultSupplier implements DefaultValueSupplier {
    private static final String DEFAULT_NOW = "now";

    @NonNull
    @Override
    public JsonElement getDefaultValue(@NonNull Column column) {
        final var dateTimeDefault = column.getDatetimeDefault();
        return DEFAULT_NOW.equals(dateTimeDefault)
                ? new JsonPrimitive(LocalTime.now().format(DateTimeFormatter.ISO_TIME))
                : JsonNull.INSTANCE;
    }
}
