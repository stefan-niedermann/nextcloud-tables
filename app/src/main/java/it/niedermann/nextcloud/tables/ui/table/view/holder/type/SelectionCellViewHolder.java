package it.niedermann.nextcloud.tables.ui.table.view.holder.type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellSelectionBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class SelectionCellViewHolder extends CellViewHolder {

    private final TableviewCellSelectionBinding binding;

    public SelectionCellViewHolder(@NonNull TableviewCellSelectionBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            binding.check.setChecked(false);
        } else {
            final var checked = Boolean.parseBoolean(String.valueOf(data.getValue()));
            binding.check.setChecked(checked);
        }
    }
}
