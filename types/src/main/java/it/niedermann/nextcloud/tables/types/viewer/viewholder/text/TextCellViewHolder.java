package it.niedermann.nextcloud.tables.types.viewer.viewholder.text;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public class TextCellViewHolder extends CellViewHolder {
    protected final TableviewCellBinding binding;

    public TextCellViewHolder(@NonNull TableviewCellBinding binding,
                              @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        binding.data.setText(value.isJsonNull() ? null : value.getAsString());

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
