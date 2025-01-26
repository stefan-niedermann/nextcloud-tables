package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.selection;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class SelectionMultiViewHolder extends SelectionViewHolder {

    public SelectionMultiViewHolder(@NonNull TableviewCellBinding binding,
                                    @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    protected String formatValue(@NonNull FullData fullData,
                                 @NonNull Column column) {
        return Optional
                .ofNullable(fullData.getSelectionOptions())
                .map(List::stream)
                .map(Stream::sorted)
                .map(selectionOptionStream -> selectionOptionStream
                        .map(SelectionOption::getLabel)
                        .collect(Collectors.joining(", ")))
                .orElse(null);
    }
}
