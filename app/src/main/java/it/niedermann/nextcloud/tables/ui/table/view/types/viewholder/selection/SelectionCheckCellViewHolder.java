package it.niedermann.nextcloud.tables.ui.table.view.types.viewholder.selection;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.ui.table.view.types.CellViewHolder;

public class SelectionCheckCellViewHolder extends CellViewHolder {

    private final TableviewCellCheckBinding binding;

    public SelectionCheckCellViewHolder(@NonNull TableviewCellCheckBinding binding,
                                        @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull FullData fullData, @NonNull Column column) {
        final var checked = Optional
                .ofNullable(fullData.getData())
                .map(Data::getValue)
                .map(Value::getBooleanValue)
                .orElse(false);

        binding.check.setChecked(checked);
    }
}
