package it.niedermann.nextcloud.tables.types.viewer.viewholder.number;

import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.BuildConfig;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public class NumberCellViewHolder extends CellViewHolder {

    private final TableviewCellBinding binding;

    public NumberCellViewHolder(@NonNull TableviewCellBinding binding,
                                @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        Number val;

        if(value.isJsonNull()) {
            val = null;
        } else {
            try {
                val = value.getAsLong();
            } catch (NumberFormatException noLongException) {
                try {
                    val = value.getAsDouble();
                } catch (NumberFormatException noDoubleException) {
                    val = null;
                    noDoubleException.printStackTrace();
                    if (BuildConfig.DEBUG) {
                        throw new IllegalArgumentException("Could not parse number " + value.getAsString(), noDoubleException);
                    }
                }
            }
        }

        // TODO respect {@link Column#getNumberDecimals()}
        binding.data.setText(val == null ? null : column.getNumberPrefix() + val + column.getNumberSuffix());

        binding.data.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
