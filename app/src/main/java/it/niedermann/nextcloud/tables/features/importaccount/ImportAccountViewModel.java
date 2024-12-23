package it.niedermann.nextcloud.tables.features.importaccount;

import android.app.Application;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.atomic.AtomicBoolean;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.exception.AccountAlreadyImportedException;
import it.niedermann.nextcloud.tables.repository.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatus;

@MainThread
public class ImportAccountViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;

    private final AtomicBoolean importInProgress = new AtomicBoolean(false);
    private final MutableLiveData<SyncStatus> syncStatus$ = new MutableLiveData<>();

    public ImportAccountViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
    }

    @AnyThread
    @NonNull
    public LiveData<SyncStatus> createAccount(@NonNull Account accountToCreate) {
        if (importInProgress.getAndSet(true)) {
            return new MutableLiveData<>(
                    new SyncStatus(accountToCreate)
                            .withError(new IllegalStateException("Account Import already in progress. Next import can only get started after SyncStatus reaches " + SyncStatus.Step.FINISHED + " or " + SyncStatus.Step.ERROR)));
        }

        return new ReactiveLiveData<>(accountRepository.createAccount(accountToCreate))
                .tap(syncStatus -> {
                    if (syncStatus.isFinished()) {
                        importInProgress.set(false);
                    }
                });

//        return new ReactiveLiveData<>(accountRepository.createAccount(accountToCreate));
//                .map(ImportAccountUIState::new);

//        return syncStatus$;
    }

//    public LiveData<SyncStatus> getSyncStatus$() {
//        return this.syncStatus$;
//    }

    public static class ImportAccountUIState {
        private final SyncStatus syncStatus;

        private ImportAccountUIState(@NonNull SyncStatus syncStatus) {
            this.syncStatus = syncStatus;
        }

        @NonNull
        public Account getAccount() {
            return syncStatus.getAccount();
        }

        public boolean isProgressIndeterminate() {
            return SyncStatus.Step.PROGRESS.equals(syncStatus.getStep());
        }

        public int getProgressTotal() {
            return 100;
        }

        public int getProgress() {
            return syncStatus.getTablesFinishedCount().orElse(0);
        }

        public int getSecondaryProgress() {
            return syncStatus.getTablesInProgress().size();
        }

        @Nullable
        public Throwable getError() {
            return syncStatus.getError();
        }

        @Nullable
        @StringRes
        public Integer getStatusTextRes() {
            return switch (syncStatus.getStep()) {
                case START -> R.string.import_state_import_account;
                case PROGRESS -> R.string.import_state_import_tables;
                case FINISHED -> null;
                case ERROR -> {
                    if (syncStatus.getError() instanceof AccountAlreadyImportedException) {
                        yield R.string.account_already_imported;
                    }

                    if (syncStatus.getError() != null) {
                        syncStatus.getError().printStackTrace();
//                            ExceptionDialogFragment.newInstance(syncStatus.getError(), syncStatus.getAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());

                        if (syncStatus.getError() instanceof ServerNotAvailableException) {
                            yield ((ServerNotAvailableException) syncStatus.getError()).getReason().messageRes;
                        }

//                        yield syncStatus.getError().getMessage();
                        yield null;
                    }

                    yield R.string.hint_error_appeared;
//                            new IllegalStateException("Received error step while importing, but exception was null").printStackTrace();
                }
            };
        }
    }
}
