package it.niedermann.nextcloud.tables.features.row;

import static java.util.Collections.emptyMap;
import static java.util.concurrent.CompletableFuture.completedFuture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResultEntry;
import it.niedermann.nextcloud.tables.repository.SearchRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditRowViewModel extends AndroidViewModel {

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
                                             @NonNull Collection<FullData> fullDataSet) {
        final var row = new Row();
        row.setCreatedBy(account.getUserName());
        row.setCreatedAt(Instant.now());
        row.setLastEditBy(account.getUserName());
        row.setLastEditAt(row.getCreatedAt());
        row.setTableId(table.getId());
        return tablesRepository.createRow(account, table, row, fullDataSet);
    }

    public CompletableFuture<Void> updateRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<FullData> fullDataSet) {
        return tablesRepository.updateRow(account, table, row, fullDataSet);
    }

    public CompletableFuture<Map<Long, FullData>> getFullData(@Nullable Long rowId) {
        return rowId == null
                ? completedFuture(emptyMap())
                : tablesRepository.getRawColumnIdAndFullData(rowId);
    }

    @NonNull
    public LiveData<Collection<Pair<SearchProvider, OcsSearchResultEntry>>> getSearchResultProposals(@NonNull Account account,
                                                                                                     @NonNull Column column,
                                                                                                     @NonNull String term) {

        final var searchProviderIds = Optional.of(column)
                .map(Column::getTextAttributes)
                .map(TextAttributes::textAllowedPattern)
                .map(pattern -> pattern.split(","))
                .map(Set::of)
                .orElseGet(Collections::emptySet);

        return searchRepository.search(account, searchProviderIds, term);
    }

    @NonNull
    public LiveData<Collection<OcsAutocompleteResult>> getAutocompleteProposals(@NonNull Account account,
                                                                                @NonNull Column column,
                                                                                @NonNull String term) {
        return new ReactiveLiveData<>(searchRepository.searchUser(account, column.getUserGroupAttributes(), term))
                .map(results -> results);
    }
}
