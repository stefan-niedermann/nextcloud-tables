package it.niedermann.nextcloud.tables.repository.defaults.supplier.selection;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class SelectionCheckDefaultSupplier extends DefaultValueSupplier {

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var value = fullData.getData().getValue().getBooleanValue();

        if (value == null) {
            final var defaultValue = fullColumn.getColumn().getDefaultValue().getBooleanValue();
            Optional.ofNullable(defaultValue)
                    .ifPresent(fullData.getData().getValue()::setBooleanValue);
        }
    }
}
