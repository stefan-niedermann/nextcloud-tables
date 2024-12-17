package it.niedermann.nextcloud.tables.ui.table.view.types;

import android.view.View;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.databinding.TableviewRowHeaderBinding;

public class RowHeaderViewHolder extends AbstractViewHolder {
    public final TableviewRowHeaderBinding binding;

    public RowHeaderViewHolder(@NonNull TableviewRowHeaderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Row row) {
        this.binding.sync.setVisibility(row.getStatus() == DBStatus.VOID ? View.INVISIBLE : View.VISIBLE);
        itemView.requestLayout();
    }
}
