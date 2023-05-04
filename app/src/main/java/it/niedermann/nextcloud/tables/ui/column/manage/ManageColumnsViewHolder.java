package it.niedermann.nextcloud.tables.ui.column.manage;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.ItemColumnBinding;

public class ManageColumnsViewHolder extends RecyclerView.ViewHolder {

    private final ItemColumnBinding binding;

    public ManageColumnsViewHolder(@NonNull ItemColumnBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Column column, @NonNull Consumer<Column> onEdit) {
        binding.title.setText(column.getTitle());

        if (TextUtils.isEmpty(column.getDescription())) {
            binding.description.setText(null);
            binding.description.setVisibility(View.GONE);

        } else {
            binding.description.setText(column.getDescription());
            binding.description.setVisibility(View.VISIBLE);
        }

        binding.edit.setOnClickListener(v -> onEdit.accept(column));
    }
}
