package it.niedermann.nextcloud.tables.ui.table.view.types.viewholder.number;

import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.ui.table.view.types.CellViewHolder;

public class NumberCellViewHolder extends CellViewHolder {

    private final TableviewCellBinding binding;

    public NumberCellViewHolder(@NonNull TableviewCellBinding binding,
                                @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull FullData fullData, @NonNull Column column) {
        final var value = Optional
                .ofNullable(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .flatMap(doubleValue -> Optional
                        .ofNullable(column.getNumberAttributes())
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
}
