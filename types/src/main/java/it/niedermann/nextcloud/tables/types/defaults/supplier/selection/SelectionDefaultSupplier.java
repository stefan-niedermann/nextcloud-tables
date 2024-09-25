package it.niedermann.nextcloud.tables.types.defaults.supplier.selection;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class SelectionDefaultSupplier implements DefaultValueSupplier {

    @NonNull
    @Override
    public JsonElement getDefaultValue(@NonNull Column column) {
        final var defaultValue = column.getSelectionDefault();

        return defaultValue == null || defaultValue == JsonNull.INSTANCE
                ? JsonNull.INSTANCE
                : defaultValue;
    }
}
