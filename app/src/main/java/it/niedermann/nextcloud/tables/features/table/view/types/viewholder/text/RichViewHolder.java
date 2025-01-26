package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.text;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.TableviewCellRichBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class RichViewHolder extends CellViewHolder {

    protected final TableviewCellRichBinding binding;

    public RichViewHolder(@NonNull TableviewCellRichBinding binding,
                          @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull FullData fullData, @NonNull Column column) {
        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getStringValue)
                .map(str -> HtmlCompat.fromHtml(str, 0))
                .orElse(null);

        binding.rich.setMarkdownString(value);

        binding.rich.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.rich.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
