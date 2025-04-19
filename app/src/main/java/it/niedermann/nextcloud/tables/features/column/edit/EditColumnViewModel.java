package it.niedermann.nextcloud.tables.features.column.edit;

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.SearchRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

@MainThread
public class EditColumnViewModel extends AndroidViewModel {

    private final TablesRepository tablesRepository;
    private final SearchRepository searchRepository;

    public EditColumnViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
        searchRepository = new SearchRepository(application);
    }

    @NonNull
    public CompletableFuture<Void> createColumn(@NonNull Account account, @NonNull Table table, @NonNull Column column) {
        return tablesRepository.createColumn(account, table, column);
    }

    @NonNull
    public CompletableFuture<Void> updateColumn(@NonNull Account account, @NonNull Table table, @NonNull Column column) {
        return tablesRepository.updateColumn(account, table, column);
    }

    @NonNull
    public LiveData<List<SearchProvider>> getSearchProvider(long accountId) {
        return searchRepository.getSearchProvider(accountId);
    }
}
