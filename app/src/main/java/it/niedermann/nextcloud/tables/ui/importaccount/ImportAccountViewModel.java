package it.niedermann.nextcloud.tables.ui.importaccount;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class ImportAccountViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;
    private final MutableLiveData<ImportState> importState$ = new MutableLiveData<>();

    public ImportAccountViewModel(@NonNull Application application) {
        super(application);
        this.executor = Executors.newSingleThreadExecutor();
        this.accountRepository = new AccountRepository(application);
        this.tablesRepository = new TablesRepository(application);
    }

    public void createAccount(@NonNull Account accountToCreate) {
        executor.submit(() -> {
            Account account = null;
            try {
                account = accountRepository.createAccount(accountToCreate);

                importState$.postValue(new ImportState(accountToCreate));
                accountRepository.synchronizeAccount(account);

                importState$.postValue(new ImportState(account, 0f));
                tablesRepository.synchronizeTables(account);

                importState$.postValue(new ImportState(ImportState.State.FINISHED, account));
                accountRepository.setCurrentAccount(account);

            } catch (Exception e) {
                importState$.postValue(new ImportState(account, e));
                if (account != null) {
                    accountRepository.deleteAccount(account);
                }
            }
        });
    }

    public LiveData<ImportState> getImportState() {
        return this.importState$;
    }

    public static class ImportState {
        @NonNull
        public final State state;
        @Nullable
        public final Account account;
        @Nullable
        public final Exception error;
        @Nullable
        public final Float progress;

        public ImportState(@NonNull State state, @NonNull Account account) {
            this(state, account, null, null);
        }

        public ImportState(@NonNull Account account) {
            this(State.IMPORTING_ACCOUNT, account, null, null);
        }

        public ImportState(@NonNull Account account, float progress) {
            this(State.IMPORTING_TABLES, account, progress, null);
        }

        public ImportState(@Nullable Account account, @NonNull Exception error) {
            this(State.ERROR, account, null, error);
        }

        private ImportState(@NonNull State state,
                            @Nullable Account account,
                            @Nullable Float progress,
                            @Nullable Exception error) {
            this.state = state;
            this.account = account;
            this.progress = progress;
            this.error = error;
        }

        public enum State {
            IMPORTING_ACCOUNT,
            IMPORTING_TABLES,
            FINISHED,
            ERROR,
        }
    }
}
