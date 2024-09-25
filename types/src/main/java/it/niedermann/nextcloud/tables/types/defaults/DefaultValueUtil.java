package it.niedermann.nextcloud.tables.types.defaults;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;

public class DefaultValueUtil {

    /**
     * Ensures the given data property is not null. In case its value is null, the value will be
     * initialized with the default value according the given column.
     */
    @NonNull
    public Data ensureDataObjectPresent(@NonNull Column column,
                                        @Nullable Data data,
                                        @NonNull DefaultValueSupplier defaultValueSupplier) {
        final Data dataToPass;

        if (data != null) {
            dataToPass = data;

            final var value = data.getValue();
            if (value == null) {
                final var defaultValue = defaultValueSupplier.getDefaultValue(column);
                dataToPass.setValue(defaultValue);
            }

        } else {
            dataToPass = new Data();
            dataToPass.setAccountId(column.getAccountId());
            dataToPass.setColumnId(column.getId());
            dataToPass.setRemoteColumnId(column.getRemoteId());
            final var defaultValue = defaultValueSupplier.getDefaultValue(column);
            dataToPass.setValue(defaultValue);
        }

        return dataToPass;
    }
}
