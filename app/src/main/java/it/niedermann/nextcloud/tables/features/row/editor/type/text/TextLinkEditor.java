package it.niedermann.nextcloud.tables.features.row.editor.type.text;

import static java.util.function.Predicate.not;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.bumptech.glide.load.model.GlideUrl;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.LinkValue;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.LinkValueWithProviderId;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;
import it.niedermann.nextcloud.tables.features.row.editor.type.AutocompleteEditViewWithDefaultDropdown;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResultEntry;
import it.niedermann.nextcloud.tables.util.TextLinkUtil;

public class TextLinkEditor extends AutocompleteEditViewWithDefaultDropdown<Pair<SearchProvider, OcsSearchResultEntry>> {

    public TextLinkEditor(@NonNull Context context) {
        super(context);
    }

    public TextLinkEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextLinkEditor(@NonNull Account account,
                          @NonNull Context context,
                          @NonNull Column column,
                          @NonNull ProposalProvider<Pair<SearchProvider, OcsSearchResultEntry>> proposalProvider) {
        super(account, context, column, proposalProvider, R.drawable.baseline_link_24);
    }

    @Nullable
    @Override
    protected String fullDataToDropDownString() {
        final var context = binding.getRoot().getContext();
        return Optional
                .ofNullable(fullData)
                .map(FullData::getLinkValueWithProviderRemoteId)
                .map(LinkValueWithProviderId::getLinkValue)
                .map(value -> TextLinkUtil.getLinkAsDisplayValue(context, value))
                .orElse(null);
    }

    @Override
    protected void writeSelectedValueToModel(@Nullable Pair<SearchProvider, OcsSearchResultEntry> proposal) {
        assert fullData != null;

        if (proposal == null) {
            fullData.getData().getValue().setLinkValueRef(null);
            fullData.setLinkValueWithProviderRemoteId(null);
            return;
        }

        final var searchProvider = Optional.of(proposal).map(p -> p.first);
        final var entry = Optional.of(proposal).map(p -> p.second);

        final var linkValue = new LinkValue();
        linkValue.setDataId(fullData.getData().getId());
        final var value = entry
                .map(OcsSearchResultEntry::resourceUrl)
                .filter(not(String::isBlank))
                .map(Uri::parse);

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Can not map proposal because resourceUrl is empty");
        }

        final var subline = entry
                .map(OcsSearchResultEntry::subline)
                .filter(not(String::isBlank))
                .orElse(searchProvider
                        .map(SearchProvider::getName)
                        .orElse(null));

        final var title = entry
                .map(OcsSearchResultEntry::title)
                .filter(not(String::isBlank))
                .orElse(subline);

        linkValue.setTitle(title);
        linkValue.setSubline(subline);
        linkValue.setValue(value.get());

        searchProvider
                .map(SearchProvider::getId)
                .ifPresent(linkValue::setProviderId);

        final var linkValueWithProviderRemoteId = new LinkValueWithProviderId();
        linkValueWithProviderRemoteId.setLinkValue(linkValue);

        searchProvider
                .map(SearchProvider::getRemoteId)
                .ifPresent(linkValueWithProviderRemoteId::setProviderId);

        fullData.setLinkValueWithProviderRemoteId(linkValueWithProviderRemoteId);
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        final var errorMessages = super.validate();

        if (errorMessages.isPresent()) {
            return errorMessages;
        }

        final boolean valid = !column.isMandatory() || Optional
                .ofNullable(fullData)
                .map(FullData::getLinkValueWithProviderRemoteId)
                .map(LinkValueWithProviderId::getLinkValue)
                .map(LinkValue::getValue)
                .isPresent();

        return valid
                ? Optional.empty()
                : Optional.of(getContext().getString(R.string.validation_mandatory));
    }

    @Override
    protected Optional<String> getTitle(@Nullable Pair<SearchProvider, OcsSearchResultEntry> pair) {
        final var item = Optional.ofNullable(pair);
        final var entry = item.map(p -> p.second);
        return entry
                .map(OcsSearchResultEntry::title)
                .filter(not(String::isBlank))
                .or(() -> entry
                        .map(OcsSearchResultEntry::subline)
                        .filter(not(String::isBlank))
                        .or(() -> item.map(p -> p.first)
                                .map(SearchProvider::getName)));
    }

    @Override
    protected Optional<String> getSubline(@Nullable Pair<SearchProvider, OcsSearchResultEntry> item) {
        final var optionalItem = Optional.ofNullable(item);
        final var searchProvider = optionalItem.map(p -> p.first);
        final var entry = optionalItem.map(p -> p.second);
        return entry
                .map(OcsSearchResultEntry::resourceUrl)
                .filter(not(String::isBlank))
                .or(() -> entry
                        .map(OcsSearchResultEntry::subline)
                        .filter(not(String::isBlank))
                        .or(() -> searchProvider
                                .map(SearchProvider::getName)));
    }

    @Override
    protected Optional<ThumbDescriptor> getThumb(@Nullable Pair<SearchProvider, OcsSearchResultEntry> item, int size) {
        final var entry = Optional.ofNullable(item).map(p -> p.second);
        return entry
                .map(OcsSearchResultEntry::thumbnailUrl)
                .or(() -> entry.map(OcsSearchResultEntry::icon)
                        .filter(not(TextUtils::isEmpty))
                        .map(i -> account.getUrl() + i))
                .filter(not(TextUtils::isEmpty))
                .map(GlideUrl::new)
                .map(url -> new ThumbDescriptor(url, R.drawable.baseline_link_24, it.niedermann.android.markdown.R.drawable.ic_baseline_broken_image_24));
    }
}
