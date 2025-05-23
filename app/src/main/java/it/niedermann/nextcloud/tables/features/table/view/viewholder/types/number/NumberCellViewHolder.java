package it.niedermann.nextcloud.tables.features.table.view.viewholder.types.number;

import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.CellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class NumberCellViewHolder extends CellViewHolder {

    private final TableviewCellBinding binding;

    public NumberCellViewHolder(@NonNull TableviewCellBinding binding,
                                @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull Account account,
                     @NonNull FullData fullData,
                     @NonNull FullColumn fullColumn) {
        final var column = fullColumn.getColumn();
        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .flatMap(doubleValue -> Optional
                        .of(column.getNumberAttributes())
                        .map(NumberAttributes::numberDecimals)
                        .map(decimals -> "%." + decimals + "f")
                        .map(formatString -> String.format(Locale.getDefault(), formatString, doubleValue))
                        .map(formattedString -> column.getNumberAttributes().numberPrefix() + formattedString + column.getNumberAttributes().numberSuffix()));

        binding.data.setText(value.orElse(null));

        binding.data.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }

    @Override
    public void bindPending() {
        binding.data.setText(R.string.simple_loading);
    }
}
