package it.niedermann.nextcloud.tables.ui.importaccount;

import android.app.Application;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatus;

@MainThread
public class ImportAccountViewModel extends AndroidViewModel {

    private static final String TAG = ImportAccountViewModel.class.getSimpleName();

    private final AccountRepository accountRepository;
    private final MediatorLiveData<ImportAccountUIState> state$ = new MediatorLiveData<>(new ImportAccountUIState());
    private volatile LiveData<ImportAccountUIState> currentImport$ = null;

    public ImportAccountViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
    }

    public LiveData<ImportAccountUIState> getState$() {
        return this.state$;
    }

    public void accountSelectionStarted() {
        synchronized (ImportAccountViewModel.this) {

            if (currentImport$ != null) {
                throw new IllegalStateException("Account Import already in progress. Next import can only get started after SyncStatus reaches " + SyncStatus.Step.FINISHED + " or " + SyncStatus.Step.ERROR);
            }

            this.state$.setValue(new ImportAccountUIState(true));
        }
    }

    public void accountSelectionFinished(@NonNull Account accountToCreate) {
        synchronized (ImportAccountViewModel.this) {

            if (currentImport$ == null) {
                this.currentImport$ = new ReactiveLiveData<>(accountRepository.createAccount(accountToCreate))
                        .map(syncStatus -> new ImportAccountUIState(getApplication(), syncStatus))
                        .tap(syncStatus -> Log.v(TAG, syncStatus.toString()));

            } else {
                throw new IllegalStateException("Account Import already in progress. Next import can only get started after SyncStatus reaches " + SyncStatus.Step.FINISHED + " or " + SyncStatus.Step.ERROR);
            }

            this.state$.addSource(this.currentImport$, state -> {

                synchronized (ImportAccountViewModel.this) {
                    if (!state.importRunning()) {
                        this.state$.removeSource(this.currentImport$);
                        this.currentImport$ = null;
                    }
                }

            });

        }
    }

    public void accountSelectionFinished(@NonNull Throwable t) {
        synchronized (ImportAccountViewModel.this) {

            if (currentImport$ != null) {
                throw new IllegalStateException("Account Import already in progress. Next import can only get started after SyncStatus reaches " + SyncStatus.Step.FINISHED + " or " + SyncStatus.Step.ERROR);
            }

            this.state$.setValue(new ImportAccountUIState(t));
        }
    }
}
