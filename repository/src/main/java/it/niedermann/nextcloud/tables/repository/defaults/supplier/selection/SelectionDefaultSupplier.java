package it.niedermann.nextcloud.tables.repository.defaults.supplier.selection;

import static java.util.function.Predicate.not;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class SelectionDefaultSupplier extends DefaultValueSupplier {

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var value = fullData.getSelectionOptions();

        if (value == null || value.isEmpty()) {
            final var defaultValue = fullColumn.getDefaultSelectionOptions();
            Optional.of(defaultValue)
                    .filter(not(List::isEmpty))
                    .flatMap(selectionOptions -> selectionOptions.stream().findAny())
                    .map(List::of)
                    .ifPresent(fullData::setSelectionOptions);
        }
    }
}
