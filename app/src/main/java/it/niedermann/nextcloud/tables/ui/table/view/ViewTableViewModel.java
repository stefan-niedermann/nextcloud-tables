package it.niedermann.nextcloud.tables.ui.table.view;

import static java.util.Collections.emptyList;
import static java.util.Map.Entry.comparingByKey;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
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
            } catch (Exception e) {
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
        return Transformations.map(tablesRepository.getData(table), data -> {
            final var allPresentColumnIds = data.stream().map(Data::getColumnId).collect(toUnmodifiableSet());
            Log.i("Foo", "Table "+ table.getTitle() + " - columnIds: " + allPresentColumnIds);
            final var rowColumnMap = data.stream().collect(groupingBy(Data::getRowId, groupingBy(Data::getColumnId)));
            final var rowColumnList = normalizeRowColumnMap(rowColumnMap, allPresentColumnIds);
            return unwrapData(rowColumnList);
        });
    }

    @NonNull
    private List<List<List<Data>>> normalizeRowColumnMap(@NonNull Map<Long, Map<Long, List<Data>>> rowColumnMap, Set<Long> allPresentColumnIds) {
        return rowColumnMap
                .values()
                .stream()
                .map(rowEntry -> {
                    allPresentColumnIds
                            .stream()
                            .filter(not(rowEntry::containsKey))
                            .forEach(columnId -> rowEntry.put(columnId, emptyList()));

                    return rowEntry
                            .entrySet()
                            .stream()
                            .sorted(comparingByKey())
                            .map(Map.Entry::getValue)
                            .collect(toUnmodifiableList());
                })
                .collect(toUnmodifiableList());
    }

    @NonNull
    private List<List<Data>> unwrapData(@NonNull List<List<List<Data>>> rowColumnList) {
        return rowColumnList
                .stream()
                .map(columnList -> {
                    final var newColumnList = new ArrayList<Data>(columnList.size());
                    for (final var column : columnList) {
                        if (column.isEmpty()) {
                            newColumnList.add(null);
                        } else if (column.size() == 1) {
                            newColumnList.add(column.get(0));
                        } else {
                            throw new RuntimeException("Expected column to have 0 or 1 data entries but it had " + column.size());
                        }
                    }
                    return newColumnList;
                })
                .collect(toUnmodifiableList());
    }
}
