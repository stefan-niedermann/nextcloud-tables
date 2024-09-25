package it.niedermann.nextcloud.tables.types.defaults.supplier.text;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class TextDefaultSupplier implements DefaultValueSupplier {
    @NonNull
    @Override
    public JsonElement getDefaultValue(@NonNull Column column) {
        final var textDefault = column.getTextDefault();
        return textDefault == null ? JsonNull.INSTANCE : new JsonPrimitive(textDefault);
    }
}
