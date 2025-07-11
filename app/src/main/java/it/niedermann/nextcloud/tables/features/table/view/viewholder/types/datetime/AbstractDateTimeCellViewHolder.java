package it.niedermann.nextcloud.tables.features.table.view.viewholder.types.datetime;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.CellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public abstract class AbstractDateTimeCellViewHolder extends CellViewHolder {

    private final TableviewCellBinding binding;

    public AbstractDateTimeCellViewHolder(@NonNull TableviewCellBinding binding,
                                          @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull Account account, @NonNull FullData fullData, @NonNull FullColumn fullColumn) {
        final var value = Optional
                .of(fullData.getData())
                .map(this::formatValue)
                .orElse(null);

        binding.data.setText(value);

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }

    protected abstract String formatValue(@NonNull Data data);

    @Override
    public void bindPending() {
        binding.data.setText(R.string.simple_loading);
    }
}
