package it.niedermann.nextcloud.tables.types.defaults.supplier.usergroup;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public class UserGroupDefaultSupplier implements DefaultValueSupplier {
    @NonNull
    @Override
    public JsonElement getDefaultValue(@NonNull Column column) {
        return JsonNull.INSTANCE;
    }
}
