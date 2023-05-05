package it.niedermann.nextcloud.tables.ui.table.view.holder.type.number;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.widget.LinearLayout;

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
            setText(column, String.valueOf(column.getNumberDefault()));
        } else {
            setText(column, String.valueOf(data.getValue()));
        }

        binding.data.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }

    @SuppressLint("SetTextI18n")
    private void setText(@NonNull Column column, String number) {
        String parsedNumber;

        // TODO respect {@link Column#getNumberDecimals()}

        try {
            parsedNumber = String.valueOf(Long.parseLong(number));
        } catch (NumberFormatException noLongException) {
            try {
                parsedNumber = String.valueOf(Double.parseDouble(number));
            } catch (NumberFormatException noDoubleException) {
                parsedNumber = number;
            }
        }

        binding.data.setText(column.getNumberPrefix() + parsedNumber + column.getNumberSuffix());
    }
}
