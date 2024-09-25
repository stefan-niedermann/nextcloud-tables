package it.niedermann.nextcloud.tables.types.viewer.viewholder.text;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellRichBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public class RichViewHolder extends CellViewHolder {

    protected final TableviewCellRichBinding binding;

    public RichViewHolder(@NonNull TableviewCellRichBinding binding,
                          @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        binding.rich.setMarkdownString(value.isJsonNull() ? null : value.getAsString());

        binding.rich.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.rich.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
