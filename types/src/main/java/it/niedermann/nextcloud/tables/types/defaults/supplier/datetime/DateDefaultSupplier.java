package it.niedermann.nextcloud.tables.types.defaults.supplier.datetime;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class DateDefaultSupplier implements DefaultValueSupplier {
    private static final String DEFAULT_TODAY = "today";

    @NonNull
    @Override
    public JsonElement getDefaultValue(@NonNull Column column) {
        final var dateTimeDefault = column.getDatetimeDefault();
        return DEFAULT_TODAY.equals(dateTimeDefault)
                ? new JsonPrimitive(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                : JsonNull.INSTANCE;
    }
}
