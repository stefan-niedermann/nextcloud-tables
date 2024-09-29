package it.niedermann.nextcloud.tables.ui.table.view;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.model.FullTable;
import it.niedermann.nextcloud.tables.model.FullTableLiveData;
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
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public LiveData<Account> getCurrentAccount() {
        return accountRepository.getCurrentAccount();
    }

    public LiveData<Pair<Account, FullTable>> getCurrentFullTable() {
        return new ReactiveLiveData<>(getCurrentAccount())
                .flatMap(account -> {
                    if (account == null) {
                        return new MutableLiveData<>();
                    }

                    if (account.getCurrentTable() == null) {
                        executor.submit(() -> accountRepository.guessCurrentTable(account));
                        return new MutableLiveData<>(new Pair<>(account, null));
                    }

                    return new ReactiveLiveData<>(tablesRepository.getNotDeletedTable$(account.getCurrentTable()))
                            .flatMap(this::getFullTable)
                            .map(fullTable -> new Pair<>(account, fullTable))
                            .distinctUntilChanged();
                });
    }

    public LiveData<FullTable> getFullTable(@Nullable Table table) {
        if (table == null) {
            return new MutableLiveData<>(null);
        }

        return new FullTableLiveData(
                table,
                tablesRepository.getNotDeletedRows$(table),
                tablesRepository.getNotDeletedColumns$(table),
                tablesRepository.getUsedSelectionOptions(table),
                tablesRepository.getData(table)
        );
    }

    public CompletableFuture<Void> deleteRow(@NonNull Table table, @NonNull Row row) {
        return supplyAsync(() -> {
            try {
                tablesRepository.deleteRow(table, row);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Void> deleteColumn(@NonNull Table table, @NonNull Column column) {
        return supplyAsync(() -> {
            try {
                tablesRepository.deleteColumn(table, column);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
}
