package it.niedermann.nextcloud.tables.ui.table.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.databinding.ItemRowBinding;

public class RowViewHolder extends RecyclerView.ViewHolder {

    private final ItemRowBinding binding;

    public RowViewHolder(@NonNull ItemRowBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Row row, @NonNull View.OnClickListener onClickListener) {
        binding.getRoot().setOnClickListener(onClickListener);
        binding.title.setText(row.getId() + "");
    }
}
