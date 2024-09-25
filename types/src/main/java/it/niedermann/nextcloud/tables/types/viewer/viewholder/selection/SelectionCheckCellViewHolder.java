package it.niedermann.nextcloud.tables.types.viewer.viewholder.selection;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public class SelectionCheckCellViewHolder extends CellViewHolder {

    private final TableviewCellCheckBinding binding;

    public SelectionCheckCellViewHolder(@NonNull TableviewCellCheckBinding binding,
                                        @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        binding.check.setChecked(!value.isJsonNull() && value.getAsBoolean());
    }
}
