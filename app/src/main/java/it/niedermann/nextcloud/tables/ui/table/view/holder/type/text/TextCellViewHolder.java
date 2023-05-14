package it.niedermann.nextcloud.tables.ui.table.view.holder.type.text;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class TextCellViewHolder extends CellViewHolder {
    protected final TableviewCellBinding binding;

    public TextCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        binding.data.setText(data == null ? null : data.getValue());

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
