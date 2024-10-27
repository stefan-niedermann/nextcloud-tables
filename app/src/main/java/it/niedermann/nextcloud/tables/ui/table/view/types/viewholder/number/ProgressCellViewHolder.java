package it.niedermann.nextcloud.tables.ui.table.view.types.viewholder.number;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.ui.table.view.types.CellViewHolder;

public class ProgressCellViewHolder extends CellViewHolder {

    private final TableviewCellProgressBinding binding;

    public ProgressCellViewHolder(@NonNull TableviewCellProgressBinding binding,
                                  @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull FullData fullData, @NonNull Column column) {
        final var attributes = Optional
                .of(column.getNumberAttributes());

        final var min = attributes
                .map(NumberAttributes::numberMin)
                .map(Double::intValue)
                .orElse(0);

        final var max = attributes
                .map(NumberAttributes::numberMax)
                .map(Double::intValue)
                .orElse(100);

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .map(Double::intValue)
                .map(val -> val < min ? min : val > max ? max : val)
                .orElse(0);

        binding.progress.setMin(min);
        binding.progress.setMax(max);
        binding.progress.setProgressCompat(value, false);
    }
}
