package it.niedermann.nextcloud.tables.features.column.manage;

import static java.util.function.Predicate.not;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Optional;
import java.util.function.Consumer;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ItemColumnBinding;

public class ManageColumnsViewHolder extends RecyclerView.ViewHolder {

    private final ItemColumnBinding binding;

    public ManageColumnsViewHolder(@NonNull ItemColumnBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull FullColumn fullColumn, @NonNull Consumer<FullColumn> onEdit) {
        final var column = fullColumn.getColumn();
        final var description = Optional.ofNullable(column.getDescription())
                .filter(not(TextUtils::isEmpty))
                .orElse(column.getDataType().toHumanReadableString(binding.getRoot().getContext()));

        binding.title.setText(column.getTitle());
        binding.description.setText(description);
        binding.edit.setOnClickListener(v -> onEdit.accept(fullColumn));
    }
}
