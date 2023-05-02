package it.niedermann.nextcloud.tables.ui.table.view;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.model.FullTable;
import it.niedermann.nextcloud.tables.model.FullTableLiveData;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class ViewTableViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;

    public ViewTableViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        tablesRepository = new TablesRepository(application);
        executor = Executors.newSingleThreadExecutor();
    }

    public CompletableFuture<Void> synchronizeAccountAndTables(@NonNull Account account) {
        return supplyAsync(() -> {
            try {
                this.accountRepository.synchronizeAccount(account);
                this.tablesRepository.synchronizeTables(account);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
            return null;
        }, executor);
    }

    public LiveData<FullTable> getFullTable(@NonNull Table table) {
        return distinctUntilChanged(
                new FullTableLiveData(
                        tablesRepository.getNotDeletedRows$(table),
                        tablesRepository.getNotDeletedColumns$(table),
                        tablesRepository.getData(table)
                )
        );
    }

    public void deleteRow(@NonNull Row row) {
        executor.submit(() -> {
            try {
                tablesRepository.deleteRow(row);
            } catch (NextcloudFilesAppAccountNotFoundException |
                     NextcloudHttpRequestFailedException | IOException e) {
                // TODO propagate?
                e.printStackTrace();
            }
        });
    }

    public void deleteColumn(@NonNull Column column) {
        executor.submit(() -> {
            try {
                tablesRepository.deleteColumn(column);
            } catch (NextcloudFilesAppAccountNotFoundException |
                     NextcloudHttpRequestFailedException | IOException e) {
                // TODO propagate?
                e.printStackTrace();
            }
        });
    }
}
