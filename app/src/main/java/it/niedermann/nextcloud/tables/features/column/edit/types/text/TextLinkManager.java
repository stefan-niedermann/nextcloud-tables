package it.niedermann.nextcloud.tables.features.column.edit.types.text;


import static it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API.TEXT_LINK_PROVIDER_ID_URL;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageTextLinkBinding;
import it.niedermann.nextcloud.tables.features.column.edit.SearchProviderSupplier;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;

public class TextLinkManager extends ColumnEditView<ManageTextLinkBinding> {

    private final ReactiveLiveData<Long> accountId$ = new ReactiveLiveData<>();

    public TextLinkManager(@NonNull Context context) {
        super(context);
    }

    public TextLinkManager(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextLinkManager(@NonNull Context context,
                           @NonNull SearchProviderSupplier searchProviderSupplier,
                           @Nullable FragmentManager fragmentManager) {
        super(context, ManageTextLinkBinding.inflate(LayoutInflater.from(context)), fragmentManager);

        this.accountId$
                .distinctUntilChanged()
                .tap(accountId -> {
                    if (accountId == null) {
                        binding.searchProvider.removeAllViews();
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(searchProviderSupplier::getSearchProvider)
                .observe(this, searchProvider -> {
                    final var columnSearchProviders = Optional.ofNullable(fullColumn.getColumn().getTextAttributes().textAllowedPattern())
                            .map(pattern -> pattern.split(","))
                            .map(Set::of)
                            .orElse(Collections.emptySet());

                    binding.searchProvider.removeAllViews();
                    binding.url.setChecked(columnSearchProviders.contains(TEXT_LINK_PROVIDER_ID_URL));

                    final var enabledSearchProviders = getVisibleSearchProviderSwitches()
                            .stream()
                            .filter(MaterialSwitch::isChecked)
                            .map(SearchProviderSwitch::getSearchProvider)
                            .map(SearchProvider::getId)
                            .collect(Collectors.toSet());

                    searchProvider
                            .stream()
                            .map(sp -> new SearchProviderSwitch(getContext(), sp))
                            .peek(sp -> sp.setChecked(
                                    columnSearchProviders.contains(sp.getSearchProvider().getRemoteId()) ||
                                    enabledSearchProviders.contains(sp.getSearchProvider().getId()))
                            )
                            .forEach(binding.searchProvider::addView);
                });
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        fullColumn.getColumn().setTextAttributes(new TextAttributes(
                Stream.concat(
                                binding.url.isChecked()
                                        ? Stream.of(TEXT_LINK_PROVIDER_ID_URL)
                                        : Stream.empty(),

                                getVisibleSearchProviderSwitches()
                                        .stream()
                                        .filter(MaterialSwitch::isChecked)
                                        .map(SearchProviderSwitch::getSearchProvider)
                                        .map(SearchProvider::getRemoteId)
                        )
                        .collect(Collectors.joining(",")),
                null
        ));

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        final var searchProviderRemoteIds = Optional.of(fullColumn.getColumn().getTextAttributes())
                .map(TextAttributes::textAllowedPattern)
                .map(pattern -> TextUtils.split(pattern, ","))
                .map(Set::of)
                .orElseGet(Collections::emptySet);

        getVisibleSearchProviderSwitches().forEach(searchProviderSwitch ->
                searchProviderRemoteIds.forEach(searchProviderRemoteId ->
                        searchProviderSwitch.setChecked(
                                Objects.equals(
                                        searchProviderSwitch.getSearchProvider().getRemoteId(),
                                        searchProviderRemoteId
                                )
                        )
                ));

        binding.url.setChecked(searchProviderRemoteIds.contains(TEXT_LINK_PROVIDER_ID_URL));

        this.accountId$.setValue(fullColumn.getColumn().getAccountId());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        getVisibleSearchProviderSwitches()
                .forEach(view -> view.setEnabled(enabled));
    }

    private Collection<SearchProviderSwitch> getVisibleSearchProviderSwitches() {
        final var searchProviderCount = binding.searchProvider.getChildCount();
        final var list = new ArrayList<SearchProviderSwitch>(searchProviderCount);

        for (int i = 0; i < searchProviderCount; i++) {
            final var child = binding.searchProvider.getChildAt(i);
            if (child instanceof SearchProviderSwitch searchProviderSwitchView) {
                list.add(searchProviderSwitchView);
            }
        }

        return list;
    }

    private static class SearchProviderSwitch extends MaterialSwitch {

        private final SearchProvider searchProvider;

        public SearchProviderSwitch(@NonNull Context context,
                                    @NonNull SearchProvider searchProvider) {
            super(context);
            this.searchProvider = searchProvider;
            setText(searchProvider.getName());
            setEnabled(isEnabled());
        }

        public SearchProvider getSearchProvider() {
            return this.searchProvider;
        }
    }
}
