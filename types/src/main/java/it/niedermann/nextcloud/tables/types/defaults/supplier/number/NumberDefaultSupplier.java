package it.niedermann.nextcloud.tables.types.defaults.supplier.number;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class NumberDefaultSupplier implements DefaultValueSupplier {
    @NonNull
    @Override
    public JsonElement getDefaultValue(@NonNull Column column) {
        final var numberDefault = column.getNumberDefault();
        return numberDefault == null ? JsonNull.INSTANCE : new JsonPrimitive(numberDefault);
    }
}
