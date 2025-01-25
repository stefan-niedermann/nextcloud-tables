package it.niedermann.nextcloud.tables.features.row;

import static java.util.Collections.emptyMap;
import static java.util.concurrent.CompletableFuture.completedFuture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResultEntry;
import it.niedermann.nextcloud.tables.repository.SearchRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditRowViewModel extends AndroidViewModel implements ProposalProvider<Collection<Pair<SearchProvider, OcsSearchResultEntry>>> {

    private final TablesRepository tablesRepository;
    private final SearchRepository searchRepository;

    public EditRowViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
        searchRepository = new SearchRepository(application);
    }

    public CompletableFuture<List<FullColumn>> getNotDeletedColumns(@NonNull Table table) {
        return tablesRepository.getNotDeletedColumns(table);
    }

    public CompletableFuture<Void> createRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Collection<DataEditView<?>> editors) {
        //noinspection DataFlowIssue
        final var data = editors.stream()
                .filter(Objects::isNull)
                .map(DataEditView::getFullData)
                .collect(Collectors.toUnmodifiableList());
        final var row = new Row();
        row.setCreatedBy(account.getUserName());
        row.setCreatedAt(Instant.now());
        row.setLastEditBy(account.getUserName());
        row.setLastEditAt(row.getCreatedAt());
        row.setTableId(table.getId());
        return tablesRepository.createRow(account, table, row, data);
    }

    public CompletableFuture<Void> updateRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<DataEditView<?>> editors) {
        final var data = editors.stream()
                .map(DataEditView::getFullData)
                .collect(Collectors.toUnmodifiableList());
        return tablesRepository.updateRow(account, table, row, data);
    }

    public CompletableFuture<Map<Long, FullData>> getFullData(@Nullable Row row) {
        return row == null
                ? completedFuture(emptyMap())
                : tablesRepository.getRawColumnIdAndFullData(row.getId());
    }

    @NonNull
    @Override
    public LiveData<Collection<Pair<SearchProvider, OcsSearchResultEntry>>> getProposals(@NonNull Account account,
                                                                                         @NonNull Column column,
                                                                                         @NonNull String term) {
        final var searchUrlLiveData = Optional.of(column)
                .map(Column::getTextAttributes)
                .map(TextAttributes::textAllowedPattern)
                .map(pattern -> pattern.split(","))
                .map(Set::of)
                .filter(set -> set.contains("url"))
                .map(v -> searchRepository.searchUrl(account, term))
                .orElseGet(MutableLiveData::new);

        return new ReactiveLiveData<>(searchRepository.search(account, column, term))
                .combineWith(() -> searchUrlLiveData)
                .map(args -> {
                            final var searchResults = args.first;
                            final var searchUrlResult = args.second;
                            final var combinedSearchResults = searchResults
                                    .entrySet()
                                    .stream()
                                    .flatMap(entry -> entry
                                            .getValue()
                                            .stream()
                                            .map(value -> new Pair<>(entry.getKey(), value)));

                            return (Collection<Pair<SearchProvider, OcsSearchResultEntry>>) Stream.concat(
                                            combinedSearchResults,
                                            Stream.of(new Pair<SearchProvider, OcsSearchResultEntry>(null, searchUrlResult)))
                                    .collect(Collectors.toUnmodifiableList());
                        }
                )
                .distinctUntilChanged();
    }
}
