package it.niedermann.nextcloud.tables.repository.defaults.supplier.datetime;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class DateTimeDefaultSupplier extends DefaultValueSupplier {

    private static final String DEFAULT_NOW = "now";

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var value = fullData.getData().getValue().getInstantValue();

        if (value == null) {
            final var defaultValue = fullColumn.getColumn().getDefaultValue().getStringValue();
            Optional.ofNullable(defaultValue)
                    .filter(DEFAULT_NOW::equals)
                    .map(str -> Instant.now())
                    .ifPresent(fullData.getData().getValue()::setInstantValue);
        }
    }
}
