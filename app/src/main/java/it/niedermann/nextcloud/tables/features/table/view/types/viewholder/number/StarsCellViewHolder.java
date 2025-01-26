package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.number;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class StarsCellViewHolder extends CellViewHolder {

    private final TableviewCellStarsBinding binding;

    public StarsCellViewHolder(@NonNull TableviewCellStarsBinding binding,
                               @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull FullData fullData,
                     @NonNull Column column) {
        final var stars = Optional
                .of(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .map(Double::intValue)
                .orElseGet(() -> Optional.of(column)
                        .map(Column::getDefaultValue)
                        .map(Value::getDoubleValue)
                        .map(Double::intValue)
                        .orElse(0));

        binding.getRoot().setStars(TablesV2API.ASSUMED_COLUMN_NUMBER_STARS_MAX_VALUE);
        binding.getRoot().setValue(stars);
    }
}
