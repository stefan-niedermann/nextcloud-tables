package it.niedermann.nextcloud.tables.repository.defaults.supplier;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class NoOpDefaultSupplier extends DefaultValueSupplier {

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        // No op
    }
}
