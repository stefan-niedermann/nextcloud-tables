package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.selection;

import static com.google.android.material.checkbox.MaterialCheckBox.STATE_INDETERMINATE;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class SelectionCheckCellViewHolder extends CellViewHolder {

    private final TableviewCellCheckBinding binding;

    public SelectionCheckCellViewHolder(@NonNull TableviewCellCheckBinding binding,
                                        @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull Account account,
                     @NonNull FullData fullData,
                     @NonNull FullColumn fullColumn) {
        final var checked = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getBooleanValue)
                .or(() -> Optional.of(fullColumn.getColumn())
                        .map(Column::getDefaultValue)
                        .map(Value::getBooleanValue))
                .orElse(false);

        binding.check.setChecked(checked);
    }

    @Override
    public void bindPending() {
        binding.check.setCheckedState(STATE_INDETERMINATE);
    }
}
