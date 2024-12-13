package it.niedermann.nextcloud.tables.repository.defaults;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;

public abstract class DefaultValueSupplier {

    /**
     * Applies a proper default value to the {@param data} if the current value is <code>null</code>.
     * The default value may be <code>null</code>, too.
     */
    @NonNull
    public FullData ensureDefaultValue(@NonNull FullColumn fullColumn,
                                       @Nullable FullData fullData) {
        final var result = Optional
                .ofNullable(fullData)
                .orElseGet(() -> new FullData(fullColumn.getColumn().getDataType()));

        final var data = result.getData();

        data.setColumnId(fullColumn.getColumn().getId());
        Optional.of(fullColumn.getColumn())
                .map(Column::getRemoteId)
                .ifPresent(data::setRemoteColumnId);

        applyDefaultValue(fullColumn, result);

        return result;
    }

    protected abstract void applyDefaultValue(@NonNull FullColumn fullColumn,
                                              @NonNull FullData fullData);
}
