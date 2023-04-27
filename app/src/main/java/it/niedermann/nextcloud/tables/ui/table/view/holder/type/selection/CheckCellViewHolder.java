package it.niedermann.nextcloud.tables.ui.table.view.holder.type.selection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class CheckCellViewHolder extends CellViewHolder {

    private final TableviewCellCheckBinding binding;

    public CheckCellViewHolder(@NonNull TableviewCellCheckBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            binding.check.setChecked(Boolean.parseBoolean(column.getSelectionDefault()));
        } else {
            final var checked = Boolean.parseBoolean(String.valueOf(data.getValue()));
            binding.check.setChecked(checked);
        }
        binding.check.requestLayout();
    }
}
