package it.niedermann.nextcloud.tables.repository.defaults.supplier.datetime;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class DateDefaultSupplier extends DefaultValueSupplier {

    private static final String DEFAULT_TODAY = "today";

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var value = fullData.getData().getValue().getDateValue();

        if (value == null) {
            final var defaultValue = fullColumn.getColumn().getDefaultValue().getStringValue();
            Optional.ofNullable(defaultValue)
                    .filter(DEFAULT_TODAY::equals)
                    .map(str -> LocalDate.now())
                    .ifPresent(fullData.getData().getValue()::setDateValue);
        }
    }
}
