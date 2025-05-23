package it.niedermann.nextcloud.tables.features.table.view.viewholder.types.text;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class LongCellViewHolder extends TextCellViewHolder {

    public LongCellViewHolder(@NonNull TableviewCellBinding binding,
                              @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    public void bind(@NonNull Account account, @NonNull FullData fullData, @NonNull FullColumn fullColumn) {
        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getStringValue)
                .map(str -> HtmlCompat.fromHtml(str, 0))
                .orElse(null);

        binding.data.setText(value);

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
