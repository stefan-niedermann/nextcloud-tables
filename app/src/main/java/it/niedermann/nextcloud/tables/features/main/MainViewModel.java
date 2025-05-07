package it.niedermann.nextcloud.tables.features.main;

import static androidx.lifecycle.Transformations.switchMap;

import android.app.Application;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

/// @noinspection UnusedReturnValue
@MainThread
public class MainViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;

    private final MutableLiveData<Boolean> isLoading$ = new MutableLiveData<>(true);
    private final LiveData<Boolean> userInitiatedSynchronizationActive;

    public MainViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        this.accountRepository = new AccountRepository(application);
        this.tablesRepository = new TablesRepository(application);

        userInitiatedSynchronizationActive = savedStateHandle.getLiveData("userInitiatedSynchronizationActive", false);
    }

    @NonNull
    public LiveData<Boolean> isLoading$() {
        return new ReactiveLiveData<>(this.isLoading$)
                .distinctUntilChanged();
    }

    @NonNull
    public LiveData<Account> getCurrentAccount() {
        return accountRepository.getCurrentAccount();
    }

    @NonNull
    public LiveData<AccountAndTable> getCurrentTable() {
        return switchMap(getCurrentAccount(), account -> {
            if (account == null) {
                return new MutableLiveData<>(new AccountAndTable(null, null));
            }

            if (account.getCurrentTable() == null) {
                return new MutableLiveData<>(new AccountAndTable(account, null));
            }

            return new ReactiveLiveData<>(tablesRepository.getNotDeletedTable$(account.getCurrentTable()))
                    .map(table -> new AccountAndTable(account, table))
                    .tap(() -> this.isLoading$.setValue(false));
        });
    }

    @NonNull
    public CompletableFuture<Void> synchronize(@NonNull Account account) {
        savedStateHandle.set("userInitiatedSynchronizationActive", true);
        return this.accountRepository.scheduleSynchronization(account)
                .whenCompleteAsync((result, exception) -> savedStateHandle.set("userInitiatedSynchronizationActive", false), ContextCompat.getMainExecutor(getApplication()));
    }

    @NonNull
    public LiveData<Boolean> isUserInitiatedSynchronizationActive() {
        return this.userInitiatedSynchronizationActive;
    }

    @NonNull
    public LiveData<TablesPerAccount> getTables() {
        return new ReactiveLiveData<>(getCurrentAccount())
                .flatMap(account -> {
                    final var result$ = new MediatorLiveData<TablesPerAccount>();

                    if (account == null) {
                        return new MutableLiveData<>(null);
                    }

                    final var resultValue = new TablesPerAccount(account);

                    result$.addSource(tablesRepository.getNotDeletedTables$(account, true, false), val -> {
                        resultValue.setFavorites(val);
                        result$.postValue(resultValue);
                    });

                    result$.addSource(tablesRepository.getNotDeletedTables$(account, false, false), val -> {
                        resultValue.setTables(val);
                        result$.postValue(resultValue);
                    });

                    result$.addSource(tablesRepository.getNotDeletedTables$(account, false, true), val -> {
                        resultValue.setArchived(val);
                        result$.postValue(resultValue);
                    });

                    return new ReactiveLiveData<>(result$);
                });
    }

    @MainThread
    @NonNull
    public CompletableFuture<Void> setCurrentTable(@NonNull Account account, @NonNull Table table) {
        final Long currentTableId = account.getCurrentTable();

        if (currentTableId == null || currentTableId != table.getId()) {
            this.isLoading$.setValue(true);
            return accountRepository.setCurrentTable(account.getId(), table.getId());
        }

        return CompletableFuture.completedFuture(null);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteTable(@NonNull Table table) {
        return tablesRepository.deleteTable(table);
    }

    @NonNull
    public CompletableFuture<Void> toggleFavorite(@NonNull Account account, @NonNull Table table) {
        final var tableClone = new Table(table);
        tableClone.setFavorite(!table.isFavorite());
        return tablesRepository.updateTable(account, tableClone);
    }

    @NonNull
    public CompletableFuture<Void> toggleArchived(@NonNull Account account, @NonNull Table table) {
        final var tableClone = new Table(table);
        tableClone.setArchived(!table.isArchived());
        return tablesRepository.updateTable(account, tableClone);
    }

    public record AccountAndTable(
            @Nullable Account account,
            @Nullable Table table
    ) {
    }

    public static class TablesPerAccount {
        @NonNull
        private final Account account;
        @NonNull
        private final List<Table> favorites = new ArrayList<>();
        @NonNull
        private final List<Table> tables = new ArrayList<>();
        @NonNull
        private final List<Table> archived = new ArrayList<>();

        public TablesPerAccount(@NonNull Account account) {
            this.account = account;
        }

        @NonNull
        public Account getAccount() {
            return account;
        }

        @NonNull
        public List<Table> getFavorites() {
            return favorites;
        }

        public void setFavorites(@NonNull List<Table> favorites) {
            this.favorites.clear();
            this.favorites.addAll(favorites);
        }

        @NonNull
        public List<Table> getTables() {
            return tables;
        }

        public void setTables(@NonNull List<Table> tables) {
            this.tables.clear();
            this.tables.addAll(tables);
        }

        @NonNull
        public List<Table> getArchived() {
            return archived;
        }

        public void setArchived(@NonNull List<Table> archived) {
            this.archived.clear();
            this.archived.addAll(archived);
        }
    }
}
