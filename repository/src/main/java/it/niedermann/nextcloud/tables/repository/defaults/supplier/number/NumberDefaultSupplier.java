package it.niedermann.nextcloud.tables.repository.defaults.supplier.number;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class NumberDefaultSupplier extends DefaultValueSupplier {

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var value = fullData.getData().getValue().getDoubleValue();

        if (value == null) {
            final var defaultValue = fullColumn.getColumn().getDefaultValue().getDoubleValue();
            fullData.getData().getValue().setDoubleValue(defaultValue);
        }
    }
}
