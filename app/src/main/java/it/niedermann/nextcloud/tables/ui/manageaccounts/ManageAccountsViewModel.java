package it.niedermann.nextcloud.tables.ui.manageaccounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.AccountRepository;

@SuppressWarnings("WeakerAccess")
public class ManageAccountsViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final AccountRepository accountRepository;

    public ManageAccountsViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Account>> getAccounts() {
        return accountRepository.getAccounts$();
    }

    public LiveData<Account> getCurrentAccount() {
        return accountRepository.getCurrentAccount();
    }

    public void setCurrentAccount(@Nullable Account account) {
        executor.submit(() -> accountRepository.setCurrentAccount(account));
    }

    public void deleteAccount(@NonNull Account account) {
        executor.submit(() -> accountRepository.deleteAccount(account));
    }
}
