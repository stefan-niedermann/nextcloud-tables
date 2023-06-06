package it.niedermann.nextcloud.tables.ui.table.view.holder.type.number;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.TablesApplication.FeatureToggle;
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
        Number value;

        if (data == null || TextUtils.isEmpty(data.getValue())) {
            value = column.getNumberDefault();
        } else {
            try {
                value = Long.parseLong(data.getValue());
            } catch (NumberFormatException noLongException) {
                try {
                    value = Double.parseDouble(data.getValue());
                } catch (NumberFormatException noDoubleException) {
                    value = null;
                    noDoubleException.printStackTrace();
                    if (FeatureToggle.STRICT_MODE.enabled) {
                        throw new IllegalArgumentException("Could not parse number " + data.getValue(), noDoubleException);
                    }
                }
            }

        }

        // TODO respect {@link Column#getNumberDecimals()}
        binding.data.setText(value == null ? null : column.getNumberPrefix() + value + column.getNumberSuffix());

        binding.data.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
