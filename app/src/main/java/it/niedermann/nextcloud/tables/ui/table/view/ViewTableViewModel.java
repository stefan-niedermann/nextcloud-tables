package it.niedermann.nextcloud.tables.ui.table.view;

import android.app.Application;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.model.FullTable;
import it.niedermann.nextcloud.tables.model.FullTableLiveData;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

@MainThread
public class ViewTableViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;

    public ViewTableViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        tablesRepository = new TablesRepository(application);
    }

    @NonNull
    public LiveData<Account> getCurrentAccount$() {
        return accountRepository.getCurrentAccount();
    }

    @NonNull
    public LiveData<Pair<Account, FullTable>> getCurrentFullTable$() {
        return new ReactiveLiveData<>(getCurrentAccount$())
                .flatMap(account -> {
                    if (account == null) {
                        return new MutableLiveData<>();
                    }

                    if (account.getCurrentTable() == null) {
                        return new MutableLiveData<>(new Pair<>(account, null));
                    }

                    return new ReactiveLiveData<>(tablesRepository.getNotDeletedTable$(account.getCurrentTable()))
                            .flatMap(this::getFullTable$)
                            .map(fullTable -> new Pair<>(account, fullTable))
                            .distinctUntilChanged();
                });
    }

    @NonNull
    public LiveData<FullTable> getFullTable$(@Nullable Table table) {
        if (table == null) {
            return new MutableLiveData<>(null);
        }

        return new FullTableLiveData(table,
                tablesRepository.getNotDeletedRows$(table),
                tablesRepository.getNotDeletedFullColumns$(table));
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> synchronize(@NonNull Account account) {
        return this.accountRepository.synchronize(account);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteRow(@NonNull Table table, @NonNull Row row) {
        return tablesRepository.deleteRow(table, row);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteColumn(@NonNull Table table, @NonNull Column column) {
        return tablesRepository.deleteColumn(table, column);
    }
}
