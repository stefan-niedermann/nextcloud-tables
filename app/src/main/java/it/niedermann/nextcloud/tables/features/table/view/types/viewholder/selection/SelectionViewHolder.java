package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.selection;

import static java.util.function.Predicate.not;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
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
    public void bind(@NonNull Account account,
                     @NonNull FullData fullData,
                     @NonNull FullColumn fullColumn) {
        binding.data.setText(formatValue(fullData, fullColumn));

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        binding.getRoot().requestLayout();
    }

    protected String formatValue(@NonNull FullData fullData,
                                 @NonNull FullColumn fullColumn) {
        return Optional
                .of(fullData.getSelectionOptions())
                .filter(not(Collection::isEmpty))
                .or(() -> Optional.of(fullColumn.getDefaultSelectionOptions()))
                .map(Collection::stream)
                .flatMap(Stream::findAny)
                .map(SelectionOption::getLabel)
                .orElse(null);
    }

    @Override
    public void bindPending() {
        binding.data.setText(R.string.simple_loading);
    }
}
