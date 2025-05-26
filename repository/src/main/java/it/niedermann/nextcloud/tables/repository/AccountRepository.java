package it.niedermann.nextcloud.tables.repository;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.preference.PreferenceManager;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceLongLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.repository.exception.AccountAlreadyImportedException;
import it.niedermann.nextcloud.tables.repository.exception.AccountNotCreatedException;
import it.niedermann.nextcloud.tables.repository.sync.report.LiveDataReporter;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatus;

@MainThread
public class AccountRepository extends AbstractRepository {

    private static final Logger logger = Logger.getLogger(AccountRepository.class.getSimpleName());
    private static final String TAG = AccountRepository.class.getSimpleName();
    private static final String SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT = "it.niedermann.nextcloud.tables.current_account";

    private final SharedPreferences sharedPreferences;

    @SuppressWarnings("FieldCanBeLocal")
    private final LiveData<Long> currentAccountId$;
    private final LiveData<Account> currentAccount$;

    public AccountRepository(@NonNull Context context) {
        super(context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        this.currentAccountId$ = new ReactiveLiveData<>(new SharedPreferenceLongLiveData(sharedPreferences, SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT, -1L))
                .distinctUntilChanged();
        this.currentAccount$ = new ReactiveLiveData<>(currentAccountId$)
                .flatMap(currentAccountId -> currentAccountId < 1
                        ? new MutableLiveData<>(null)
                        : db.getAccountDao().getAccountById$(currentAccountId))
                .distinctUntilChanged();
    }

    @NonNull
    public LiveData<Account> getCurrentAccount() {
        return currentAccount$;
    }

    public void setCurrentAccount(@Nullable Account account) {
        final var editor = this.sharedPreferences.edit();
        if (account == null) {
            logger.info("Setting current account to null. Maybe last account has been deleted?");
            editor.remove(SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT);
        } else {
            editor.putLong(SHARED_PREFERENCES_KEY_CURRENT_ACCOUNT, account.getId());
        }
        editor.apply();
    }

    @NonNull
    @AnyThread
    public CompletableFuture<Void> scheduleSynchronization(@NonNull Account account) {
        return super.scheduleSynchronization(account);
    }

    @NonNull
    public LiveData<Map<String, TablesVersion>> getTablesServerVersion() {
        return this.db.getAccountDao().getTablesServerVersion();
    }

    @AnyThread
    @NonNull
    public LiveData<SyncStatus> createAccount(@NonNull Account accountToCreate) {
        final var reporter = new LiveDataReporter(accountToCreate);
        completedFuture(accountToCreate)
                .thenApplyAsync(db.getAccountDao()::insert, db.getUserInteractionWriteExecutor())
                .thenAcceptAsync(accountToCreate::setId, workExecutor)
                .thenApplyAsync(v -> accountToCreate, workExecutor)
                .handleAsync((account, throwable) -> {
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

                    return account;
                }, workExecutor)
                .thenComposeAsync(account -> scheduleSynchronization(account, reporter), workExecutor)
                .thenApplyAsync(v -> accountToCreate, workExecutor)
                .whenCompleteAsync((account, throwable) -> {
                    if (throwable != null) {
                        final var cause = Optional
                                .ofNullable(throwable.getCause())
                                .orElse(throwable);

                        if (!(cause instanceof AccountNotCreatedException)) {
                            try {
                                deleteAccount(accountToCreate).get();
                            } catch (ExecutionException | InterruptedException e) {
                                reporter.report(state -> state.withError(cause));
                                return;
                            }
                        }

                        reporter.report(state -> state.withError(cause));

                    } else {
                        setCurrentAccount(account);
                        reporter.report(SyncStatus::markAsFinished);
                    }
                }, workExecutor);

        return reporter;
    }

    @NonNull
    public LiveData<List<Account>> getAccounts$() {
        return db.getAccountDao().getAccounts$();
    }

    @WorkerThread
    public List<Account> getAccounts() {
        return db.getAccountDao().getAccounts();
    }

    @NonNull
    public LiveData<List<Account>> getAccountsExcept$(long id) {
        return db.getAccountDao().getAccountsExcept$(id);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> setCurrentTable(long accountId, @Nullable Long tableId) {
        return runAsync(() -> {
            logger.info(() -> "PERF :: ----- setCurrentTable to " + tableId + " START ");
            db.getAccountDao().updateCurrentTable(accountId, tableId);
            logger.info(() -> "PERF :: ----- setCurrentTable to " + tableId + " FINISH ");
        }, db.getUserInteractionWriteExecutor());
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteAccount(@NonNull Account account) {
        // TODO Should run after Synchronization
        return runAsync(() -> db.getAccountDao().delete(account), db.getUserInteractionWriteExecutor());
    }
}
