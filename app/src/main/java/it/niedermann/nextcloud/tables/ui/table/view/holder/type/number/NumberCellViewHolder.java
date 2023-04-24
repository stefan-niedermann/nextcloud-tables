package it.niedermann.nextcloud.tables.ui.table.view.holder.type.number;

import android.annotation.SuppressLint;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.ui.table.view.holder.CellViewHolder;

public class NumberCellViewHolder extends CellViewHolder {

    private final TableviewCellBinding binding;

    public NumberCellViewHolder(@NonNull TableviewCellBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bind(@Nullable Data data, @NonNull Column column) {
        if (data == null) {
            // TODO DEFAULT
            binding.data.setText(null);
        } else {
            try {
                setText(column, String.valueOf(Long.parseLong(String.valueOf(data.getValue()))));
            } catch (NumberFormatException noLongException) {
                try {
                    setText(column, String.valueOf(Double.parseDouble(String.valueOf(data.getValue()))));
                } catch (NumberFormatException noDoubleException) {
                    setText(column, String.valueOf(data.getValue()));
                }
            }
        }
        binding.data.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        binding.data.requestLayout();
    }

    @SuppressLint("SetTextI18n")
    private void setText(@NonNull Column column, String number) {
        binding.data.setText(column.getNumberPrefix() + number + column.getNumberSuffix());
    }
}
