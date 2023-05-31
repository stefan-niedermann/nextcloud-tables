package it.niedermann.nextcloud.tables.ui.main;

import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.PreferencesRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class MainViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;
    private final PreferencesRepository preferencesRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
        this.tablesRepository = new TablesRepository(application);
        this.preferencesRepository = new PreferencesRepository(application);
        this.executor = Executors.newSingleThreadExecutor();
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

    public LiveData<Table> getCurrentTable() {
        return switchMap(getCurrentAccount(), account -> {
            if (account == null) {
                return new MutableLiveData<>(null);
            }

            if (account.getCurrentTable() == null) {
                executor.submit(() -> accountRepository.guessCurrentTable(account));
                return new MutableLiveData<>(null);
            }

            return tablesRepository.getNotDeletedTable$(account.getCurrentTable());
        });
    }

    public LiveData<Boolean> currentAccountHasTables() {
        return map(getTables(), tables -> tables != null && !tables.getOwnTables().isEmpty() && !tables.getSharedTables().isEmpty());
    }

    public LiveData<TablesPerAccount> getTables() {
        return switchMap(getCurrentAccount(), account -> {
            final var result$ = new MediatorLiveData<TablesPerAccount>();

            if (account == null) {
                return new MutableLiveData<>(null);
            }

            final var resultValue = new TablesPerAccount(account);

            result$.addSource(tablesRepository.getNotDeletedTables$(account, false), val -> {
                resultValue.setOwnTables(val);
                result$.postValue(resultValue);
            });

            result$.addSource(tablesRepository.getNotDeletedTables$(account, true), val -> {
                resultValue.setSharedTables(val);
                result$.postValue(resultValue);
            });

            return result$;
        });
    }

    public void setCurrentTable(@NonNull Table table) {
        executor.submit(() -> accountRepository.setCurrentTable(table.getAccountId(), table.getId()));
    }

    public CompletableFuture<Void> deleteTable(@NonNull Table table) {
        return supplyAsync(() -> {
            try {
                tablesRepository.deleteTable(table);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    @NonNull
    public LiveData<Pair<Account, NetworkRequest>> getAccountAndNetworkRequest() {
        return switchMap(getCurrentAccount(), account -> {
            if (account != null) {
                return map(preferencesRepository.getNetworkRequest$(), parameter -> new Pair<>(account, parameter));
            } else {
                return new MutableLiveData<>();
            }
        });
    }

    static class TablesPerAccount {
        @NonNull
        private final Account account;
        @NonNull
        private final List<Table> ownTables = new ArrayList<>();
        @NonNull
        private final List<Table> sharedTables = new ArrayList<>();

        public TablesPerAccount(@NonNull Account account) {
            this.account = account;
        }

        @NonNull
        public Account getAccount() {
            return account;
        }

        @NonNull
        public List<Table> getOwnTables() {
            return ownTables;
        }

        public void setOwnTables(@NonNull List<Table> ownTables) {
            this.ownTables.clear();
            this.ownTables.addAll(ownTables);
        }

        @NonNull
        public List<Table> getSharedTables() {
            return sharedTables;
        }

        public void setSharedTables(@NonNull List<Table> sharedTables) {
            this.sharedTables.clear();
            this.sharedTables.addAll(sharedTables);
        }
    }
}
