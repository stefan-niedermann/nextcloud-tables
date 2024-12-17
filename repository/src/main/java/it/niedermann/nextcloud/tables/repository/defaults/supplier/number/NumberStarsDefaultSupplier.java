package it.niedermann.nextcloud.tables.repository.defaults.supplier.number;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class NumberStarsDefaultSupplier extends DefaultValueSupplier {

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var value = Optional
                .ofNullable(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue);

        if (value.isEmpty()) {
            Optional
                    .ofNullable(fullColumn.getColumn())
                    .map(Column::getDefaultValue)
                    .map(Value::getDoubleValue)
                    .map(Math::ceil)
                    .ifPresent(fullData.getData().getValue()::setDoubleValue);
        }
    }
}
