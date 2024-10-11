package it.niedermann.nextcloud.tables.ui.importaccount;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;
import it.niedermann.nextcloud.tables.ui.exception.AccountAlreadyImportedException;
import it.niedermann.nextcloud.tables.ui.exception.AccountNotCreatedException;

@MainThread
public class ImportAccountViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;
    private final MutableLiveData<ImportState> importState$ = new MutableLiveData<>();

    public ImportAccountViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
        this.tablesRepository = new TablesRepository(application);
    }

    @NonNull
    public LiveData<ImportState> getImportState() {
        return this.importState$;
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Account> createAccount(@NonNull Account accountToCreate) {
        return accountRepository
                .createAccount(accountToCreate)
                .handle((account, throwable) -> {
                    final var cause = Optional.ofNullable(throwable)
                            .map(Throwable::getCause)
                            .orElse(throwable);

                    if (cause != null) {
                        if (cause instanceof SQLiteConstraintException) {
                            if (account == null) {
                                throw new AccountAlreadyImportedException((SQLiteConstraintException) cause);
                            } else {
                                throw (SQLiteConstraintException) cause;
                            }
                        } else if (cause instanceof RuntimeException) {
                            throw (RuntimeException) cause;
                        } else {
                            throw new CompletionException(cause);
                        }
                    }

                    if (account == null) {
                        throw new AccountNotCreatedException();
                    }

                    accountToCreate.setId(account.getId());
                    return account;
                })
                .thenCompose(account -> {
                    importState$.postValue(new ImportState(account));
                    return accountRepository.synchronizeAccount(account);
                })
                .thenCompose(account -> {
                    importState$.postValue(new ImportState(account, 0f));
                    return tablesRepository.synchronizeTables(account);
                })
                .handle((account, throwable) -> {
                    if (throwable != null) {
                        final var cause = Optional
                                .ofNullable(throwable.getCause())
                                .orElse(throwable);

                        importState$.postValue(new ImportState(null, cause));

                        if (!(cause instanceof AccountNotCreatedException)) {
                            try {
                                accountRepository.deleteAccount(accountToCreate).get();
                            } catch (ExecutionException | InterruptedException e) {
                                throw new CompletionException(e);
                            }
                        }

                        throw new CompletionException(cause);
                    }

                    importState$.postValue(new ImportState(ImportState.State.FINISHED, account));
                    accountRepository.setCurrentAccount(account);
                    return account;
                });
    }

    public static class ImportState {
        @NonNull
        public final State state;
        @Nullable
        public final Account account;
        @Nullable
        public final Throwable error;
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

        public ImportState(@Nullable Account account, @NonNull Throwable error) {
            this(State.ERROR, account, null, error);
        }

        private ImportState(@NonNull State state,
                            @Nullable Account account,
                            @Nullable Float progress,
                            @Nullable Throwable error) {
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
