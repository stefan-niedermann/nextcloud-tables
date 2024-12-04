package it.niedermann.nextcloud.tables.ui.importaccount;

import android.app.Application;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.model.ImportState;

@MainThread
public class ImportAccountViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;
    private final MutableLiveData<ImportState> importState$ = new MutableLiveData<>();

    public ImportAccountViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
    }

    @NonNull
    public LiveData<ImportState> getImportState() {
        return this.importState$;
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Account> createAccount(@NonNull Account accountToCreate) {
        return accountRepository.createAccount(accountToCreate, importState$);
    }
}
