package it.niedermann.nextcloud.tables.types.defaults;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Column;

public interface DefaultValueSupplier {
    @NonNull
    JsonElement getDefaultValue(@NonNull Column column);
}
