package it.niedermann.nextcloud.tables.ui.table.view.holder.type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class ProgressCellViewHolder extends CellViewHolder {

    private final TableviewCellProgressBinding binding;

    public ProgressCellViewHolder(@NonNull TableviewCellProgressBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            binding.progress.setProgressCompat(0, false);
        } else {
            try {
                final var progress = Double.parseDouble(String.valueOf(data.getValue()));
                binding.progress.setProgressCompat((int) progress, false);
            } catch (NumberFormatException noDoubleException) {
                binding.progress.setProgressCompat(0, false);
            }
        }
    }
}
