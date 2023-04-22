package it.niedermann.nextcloud.tables.ui.table.view;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.remote.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class ViewTableViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;

    public ViewTableViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        tablesRepository = new TablesRepository(application);
        executor = Executors.newSingleThreadExecutor();
    }

    public CompletableFuture<Void> synchronizeAccountAndTables(@NonNull Account account) {
        return supplyAsync(() -> {
            try {
                this.accountRepository.synchronizeAccount(account);
                this.tablesRepository.synchronizeTables(account);
            } catch (NextcloudFilesAppAccountNotFoundException | IOException |
                     NextcloudHttpRequestFailedException | ServerNotAvailableException e) {
                throw new CompletionException(e);
            }
            return null;
        }, executor);
    }

    public LiveData<List<Row>> getRows(@NonNull Table table) {
        return tablesRepository.getRows(table);
    }

    public LiveData<List<Column>> getColumns(@NonNull Table table) {
        return tablesRepository.getColumns(table);
    }

    public LiveData<List<List<Data>>> getData(@NonNull Table table) {
        return Transformations.map(tablesRepository.getData(table), data -> new ArrayList<>(data.stream()
                .collect(Collectors.groupingBy(Data::getRowId, Collectors.groupingBy(Data::getColumnId)))
                .values())
                .stream()
                .map(Map::values)
                .map(ArrayList::new)
                .collect(Collectors.toUnmodifiableList())
                .stream()
                .map(cols -> cols.stream().map(col -> {
                    if (col.size() > 1) {
                        throw new RuntimeException("");
                    } else {
                        return col.iterator().next();
                    }
                }).collect(Collectors.toUnmodifiableList()))
                .collect(Collectors.toUnmodifiableList()));
    }
}
