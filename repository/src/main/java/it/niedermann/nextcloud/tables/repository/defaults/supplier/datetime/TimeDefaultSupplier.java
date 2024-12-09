package it.niedermann.nextcloud.tables.repository.defaults.supplier.datetime;

import androidx.annotation.NonNull;

import java.time.LocalTime;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class TimeDefaultSupplier extends DefaultValueSupplier {

    private static final String DEFAULT_NOW = "now";

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var value = fullData.getData().getValue().getTimeValue();

        if (value == null) {
            final var defaultValue = fullColumn.getColumn().getDefaultValue().getStringValue();
            Optional.ofNullable(defaultValue)
                    .filter(DEFAULT_NOW::equals)
                    .map(str -> LocalTime.now())
                    .ifPresent(fullData.getData().getValue()::setTimeValue);
        }
    }
}
