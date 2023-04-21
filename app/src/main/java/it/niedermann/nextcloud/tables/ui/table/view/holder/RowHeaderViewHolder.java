package it.niedermann.nextcloud.tables.ui.table.view.holder;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.databinding.TableviewRowHeaderLayoutBinding;

public class RowHeaderViewHolder extends AbstractViewHolder {
    public final TableviewRowHeaderLayoutBinding binding;

    public RowHeaderViewHolder(@NonNull TableviewRowHeaderLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Row row) {
        this.binding.rowHeaderTextview.setText(String.valueOf(row.getRemoteId()));
    }
}
