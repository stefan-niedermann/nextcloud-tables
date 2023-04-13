package it.niedermann.nextcloud.tables.ui.accountswitcher;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.AccountRepository;

public class AccountViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
    }

    public void setCurrentAccount(@NonNull Account account) {
        accountRepository.setCurrentAccount(account);
    }

    public LiveData<Account> getCurrentAccount() {
        return accountRepository.getCurrentAccount();
    }

    public LiveData<List<Account>> getAccounts() {
        return Transformations.switchMap(getCurrentAccount(), account -> accountRepository.getAccountsExcept$(account.getId()));
    }
}
