package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.selection;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class SelectionViewHolder extends CellViewHolder {
    protected final TableviewCellBinding binding;

    public SelectionViewHolder(@NonNull TableviewCellBinding binding,
                               @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull FullData fullData,
                     @NonNull Column column) {
        binding.data.setText(formatValue(fullData, column));

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.getRoot().requestLayout();
    }

    protected String formatValue(@NonNull FullData fullData,
                                 @NonNull Column column) {
        return Optional
                .ofNullable(fullData.getSelectionOptions())
                .map(List::stream)
                .flatMap(Stream::findAny)
                .map(SelectionOption::getLabel)
                .orElse(null);
    }
}
