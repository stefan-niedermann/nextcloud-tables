package it.niedermann.nextcloud.tables.features.table.view.types.viewholder.text;

import android.net.Uri;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
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
        final var linkValue = Optional
                .of(fullData)
                .map(FullData::getLinkValueWithProviderRemoteId)
                .map(LinkValueWithProviderId::getLinkValue);

        final var value = linkValue
                .map(LinkValue::getValue)
                .map(Uri::toString);

        final var title = linkValue
                .map(LinkValue::getTitle);

        final var context = binding.getRoot().getContext();
        if (title.isPresent()) {
            binding.data.setText(context.getString(R.string.format_text_link, title.get(), value.get()));
        } else {
            binding.data.setText(value.orElse(null));
        }

        binding.data.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.data.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }
}
