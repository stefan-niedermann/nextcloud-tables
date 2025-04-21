package it.niedermann.nextcloud.tables.repository.defaults.supplier.text;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class TextDefaultSupplier extends DefaultValueSupplier {

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var value = fullData.getData().getValue();
        final var strValue = Optional.ofNullable(value.getStringValue());

        if (strValue.isEmpty()) {
            Optional.of(fullColumn.getColumn())
                    .map(Column::getDefaultValue)
                    .map(Value::getStringValue)
                    .ifPresent(value::setStringValue);
        }
    }
}
