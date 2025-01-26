package it.niedermann.nextcloud.tables.features.manageaccounts;

import android.app.Application;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.AccountRepository;

@MainThread
@SuppressWarnings("WeakerAccess")
public class ManageAccountsViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;

    public ManageAccountsViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
    }

    public LiveData<List<Account>> getAccounts() {
        return accountRepository.getAccounts$();
    }

    public LiveData<Account> getCurrentAccount() {
        return accountRepository.getCurrentAccount();
    }

    @AnyThread
    public void setCurrentAccount(@Nullable Account account) {
        accountRepository.setCurrentAccount(account);
    }

    @AnyThread
    public CompletableFuture<Void> deleteAccount(@NonNull Account account) {
        return accountRepository.deleteAccount(account);
    }
}
