package it.niedermann.nextcloud.tables.types.defaults.supplier.selection;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.SelectionDefault;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class SelectionDefaultSupplier implements DefaultValueSupplier {

    @NonNull
    @Override
    public JsonElement getDefaultValue(@NonNull Column column) {
        return Optional.ofNullable(column.getSelectionDefault())
                .map(SelectionDefault::getValue)
                .orElse(JsonNull.INSTANCE);
    }
}
