package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.text;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.LinkValueWithProviderId;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.util.TextLinkUtil;

public class LinkCellViewHolder extends TextCellViewHolder {

    public LinkCellViewHolder(@NonNull TableviewCellBinding binding,
                              @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding, defaultValueSupplier);
    }

    @Override
    public void bind(@NonNull Account account, @NonNull FullData fullData, @NonNull Column column) {

        final var context = binding.getRoot().getContext();
        final var linkValue = Optional
                .of(fullData)
                .map(FullData::getLinkValueWithProviderRemoteId)
                .map(LinkValueWithProviderId::getLinkValue)
                .map(value -> TextLinkUtil.getLinkAsDisplayValue(context, value))
                .orElse(null);

        binding.data.setText(linkValue);
        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
