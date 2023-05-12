package it.niedermann.nextcloud.tables.ui.table.view.holder.type.text;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class LineCellViewHolder extends TextCellViewHolder {

    public LineCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            binding.data.setText(column.getTextDefault());
        } else {
            binding.data.setText(data.getValue());
        }

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
