package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.number;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class ProgressCellViewHolder extends CellViewHolder {

    private final TableviewCellProgressBinding binding;

    public ProgressCellViewHolder(@NonNull TableviewCellProgressBinding binding,
                                  @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull Account account, @NonNull FullData fullData, @NonNull Column column) {
        final var min = TablesV2API.ASSUMED_COLUMN_NUMBER_PROGRESS_DEFAULT_MAX_VALUE.getLower();
        final var max = TablesV2API.ASSUMED_COLUMN_NUMBER_PROGRESS_DEFAULT_MAX_VALUE.getUpper();
        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .map(Double::intValue)
                .orElseGet(() -> Optional.of(column)
                        .map(Column::getDefaultValue)
                        .map(Value::getDoubleValue)
                        .map(Double::intValue)
                        .orElse(min));

        binding.progress.setMin(min);
        binding.progress.setMax(max);
        binding.progress.setProgressCompat(value < min ? min : value > max ? max : value, false);
        binding.progress.setIndeterminate(false);
    }

    @Override
    public void bindPending() {
        binding.progress.setIndeterminate(true);
    }
}
