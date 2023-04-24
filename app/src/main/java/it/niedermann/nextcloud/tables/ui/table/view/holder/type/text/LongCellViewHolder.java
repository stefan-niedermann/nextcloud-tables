package it.niedermann.nextcloud.tables.ui.table.view.holder.type.text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;

public class LongCellViewHolder extends TextCellViewHolder {

    public LongCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding);
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            binding.data.setText(null);
        } else {
            binding.data.setText(HtmlCompat.fromHtml(String.valueOf(data.getValue()), 0));
        }
        binding.data.requestLayout();
    }
}
