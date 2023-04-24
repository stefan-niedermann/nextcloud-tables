package it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public abstract class AbstractDateTimeCellViewHolder extends CellViewHolder {
    protected static final String DATETIME_NONE = "none";
    protected final TableviewCellBinding binding;

    public AbstractDateTimeCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
