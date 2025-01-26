package it.niedermann.nextcloud.tables.features.row.editor.type.text;

import static java.util.function.Predicate.not;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.LinkValue;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.LinkValueWithProviderId;
import it.niedermann.nextcloud.tables.databinding.EditTextLinkBinding;
import it.niedermann.nextcloud.tables.databinding.ItemLinkBinding;
import it.niedermann.nextcloud.tables.features.row.editor.OnTextChangedListener;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditViewWithProposalProvider;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResultEntry;

public class TextLinkEditor extends DataEditViewWithProposalProvider<EditTextLinkBinding, Collection<Pair<SearchProvider, OcsSearchResultEntry>>> {

    public TextLinkEditor(@NonNull Context context) {
        super(context, EditTextLinkBinding.inflate(LayoutInflater.from(context)));
    }

    public TextLinkEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, EditTextLinkBinding.inflate(LayoutInflater.from(context)));
    }

    public TextLinkEditor(@NonNull Account account,
                          @NonNull Context context,
                          @NonNull Column column,
                          @NonNull ProposalProvider<Collection<Pair<SearchProvider, OcsSearchResultEntry>>> proposalProvider) {
        super(account, context, EditTextLinkBinding.inflate(LayoutInflater.from(context)), column, proposalProvider, null);

        final var data = new ArrayList<Pair<SearchProvider, OcsSearchResultEntry>>();
        final var adapter = new LinkArrayAdapter(context, account, data, clickedEntry -> {
            binding.search.dismissDropDown();

            if (fullData == null) {
                throw new IllegalStateException("fullData was null while choosing a text link");
            }

            try {
                final var value = mapProposalToValue(clickedEntry, fullData.getData().getId());
                fullData.setLinkValueWithProviderRemoteId(value);
                onValueChanged();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        });
        binding.search.setAdapter(adapter);

        final var term$ = new ReactiveLiveData<String>();

        term$
                .observe(this, term -> {
                    final var termPresent = Optional
                            .ofNullable(term)
                            .map(String::isBlank)
                            .orElse(false);

                    if (termPresent) {
                        binding.searchWrapper.setEndIconDrawable(R.drawable.ic_baseline_check_24);
                        binding.searchWrapper.setEndIconOnClickListener(v -> {
                            // TODO Accept current link as value. We may be offline here.
                        });
                    } else {
                        Optional.ofNullable(fullData).ifPresent(f -> {
                            f.getData().getValue().setLinkValueRef(null);
                            f.setLinkValueWithProviderRemoteId(null);
                        });
                        binding.searchWrapper.setEndIconDrawable(R.drawable.ic_baseline_check_24);
                        binding.searchWrapper.setEndIconOnClickListener(v -> {
                            // TODO Accept current link as value. We may be offline here.
                        });
                    }
                });

        term$
                // TODO Check debouncing
                // .debounce(300, ChronoUnit.MILLIS)
                .filter(Objects::nonNull)
                .flatMap(term -> proposalProvider.getProposals(account, column, term))
                .observe(this, proposals -> {
                    data.clear();
                    data.addAll(proposals);
                    adapter.notifyDataSetChanged();
                });

        binding.search.addTextChangedListener((OnTextChangedListener) (s, start, before, count) -> term$.postValue(s.toString()));
        binding.searchWrapper.setEndIconOnClickListener(v -> binding.search.setText(null));
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);
        Optional.of(fullData)
                .map(FullData::getLinkValueWithProviderRemoteId)
                .map(LinkValueWithProviderId::getLinkValue)
                .map(LinkValue::getValue)
                .filter(Objects::nonNull)
                .map(Uri::toString)
                .ifPresent(binding.search::setText);
    }

    @NonNull
    private LinkValueWithProviderId mapProposalToValue(@NonNull Pair<SearchProvider, OcsSearchResultEntry> proposal, long dataId) {
        final var searchProvider = Optional.ofNullable(proposal.first);
        final var entry = Optional.of(proposal.second);

        final var linkValue = new LinkValue();
        linkValue.setDataId(dataId);
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

        return linkValueWithProviderRemoteId;
    }

    @NonNull
    @Override
    public Optional<String> validate() {

        if (column.isMandatory()) {
            final var valuePresent = Optional
                    .ofNullable(fullData)
                    .map(FullData::getLinkValueWithProviderRemoteId)
                    .map(LinkValueWithProviderId::getLinkValue)
                    .map(LinkValue::getValue)
                    .isPresent();

            return valuePresent
                    ? Optional.empty()
                    : Optional.of(getContext().getString(R.string.validation_mandatory));
        }

        return Optional.empty();
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        binding.searchWrapper.setError(message);
    }

    private static class LinkArrayAdapter extends ArrayAdapter<Pair<SearchProvider, OcsSearchResultEntry>> {

        private final Account account;
        private final List<Pair<SearchProvider, OcsSearchResultEntry>> data;
        private final Consumer<Pair<SearchProvider, OcsSearchResultEntry>> onClick;
        private final int paddingVertical;
        private final int paddingHorizontal;

        public LinkArrayAdapter(@NonNull Context context,
                                @NonNull Account account,
                                @NonNull List<Pair<SearchProvider, OcsSearchResultEntry>> data,
                                @NonNull Consumer<Pair<SearchProvider, OcsSearchResultEntry>> onClick) {
            super(context, 0, 0, data);
            this.account = account;
            this.data = data;
            this.onClick = onClick;
            this.paddingVertical = getContext().getResources().getDimensionPixelSize(it.niedermann.nextcloud.tables.ui.R.dimen.spacer_1x);
            this.paddingHorizontal = getContext().getResources().getDimensionPixelSize(it.niedermann.nextcloud.tables.ui.R.dimen.spacer_2x);
        }

        public void setData(@NonNull Collection<Pair<SearchProvider, OcsSearchResultEntry>> entries) {
            data.clear();
            data.addAll(entries);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Nullable
        @Override
        public Pair<SearchProvider, OcsSearchResultEntry> getItem(int position) {
            return data.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final var binding = convertView == null
                    ? ItemLinkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
                    : ItemLinkBinding.bind(convertView);

            final var item = Optional.ofNullable(getItem(position));
            final var searchProvider = item.map(pair -> pair.first);
            final var entry = item.map(pair -> pair.second);

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


            binding.title.setText(title);
            binding.subline.setText(subline);

            final var iconUrl = entry.map(OcsSearchResultEntry::icon)
                    .filter(not(TextUtils::isEmpty))
                    .map(i -> account.getUrl() + i)
                    .orElse(null);

            final var thumbUrl = entry
                    .map(OcsSearchResultEntry::thumbnailUrl)
                    .orElse(iconUrl);

            Optional.ofNullable(thumbUrl)
                    .ifPresent(url -> Glide.with(binding.thumb)
                            .load(url)
                            // TODO .placeholder() Maybe a spinner?
                            .error(it.niedermann.android.markdown.R.drawable.ic_baseline_broken_image_24)
                            .into(binding.thumb));

            binding.getRoot().setPaddingRelative(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            binding.getRoot().setOnClickListener(v -> onClick.accept(item.get()));

            return binding.getRoot();
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }
}
