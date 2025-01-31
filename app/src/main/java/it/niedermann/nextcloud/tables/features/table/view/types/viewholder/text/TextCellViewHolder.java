package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.text;

import android.text.util.Linkify;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class TextCellViewHolder extends CellViewHolder {
    protected final TableviewCellBinding binding;

    public TextCellViewHolder(@NonNull TableviewCellBinding binding,
                              @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull Account account, @NonNull FullData fullData, @NonNull Column column) {
        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getStringValue)
                .orElse(null);

        binding.data.setText(value);
        binding.data.setAutoLinkMask(Linkify.WEB_URLS);

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
