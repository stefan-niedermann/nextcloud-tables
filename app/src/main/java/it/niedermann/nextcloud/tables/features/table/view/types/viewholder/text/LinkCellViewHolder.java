package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.text;

import android.net.Uri;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.LinkValue;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.LinkValueWithProviderId;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class LinkCellViewHolder extends TextCellViewHolder {

    public LinkCellViewHolder(@NonNull TableviewCellBinding binding,
                              @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    public void bind(@NonNull FullData fullData, @NonNull Column column) {
        final var value = Optional
                .of(fullData)
                .map(FullData::getLinkValueWithProviderRemoteId)
                .map(LinkValueWithProviderId::getLinkValue)
                .map(LinkValue::getValue)
                .map(Uri::toString);

        binding.data.setText(value.orElse(null));

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
