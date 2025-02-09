package it.niedermann.nextcloud.tables.features.main;

import static androidx.lifecycle.Transformations.switchMap;

import android.app.Application;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
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
    public LiveData<Table> getCurrentTable() {
        return switchMap(getCurrentAccount(), account -> {
            if (account == null) {
                return new MutableLiveData<>(null);
            }

            if (account.getCurrentTable() == null) {
                return new MutableLiveData<>(null);
            }

            return new ReactiveLiveData<>(tablesRepository.getNotDeletedTable$(account.getCurrentTable()))
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

    @MainThread
    @NonNull
    public CompletableFuture<Void> setCurrentTable(@NonNull Table table) {
        this.isLoading$.setValue(true);
        return accountRepository.setCurrentTable(table.getAccountId(), table.getId());
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteTable(@NonNull Table table) {
        return tablesRepository.deleteTable(table);
    }

    public static class TablesPerAccount {
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
