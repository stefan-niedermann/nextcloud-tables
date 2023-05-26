package it.niedermann.nextcloud.tables.ui.table.edit;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditTableViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final TablesRepository tablesRepository;

    public EditTableViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public CompletableFuture<Void> createTable(@NonNull Account account, @NonNull Table table) {
        return supplyAsync(() -> {
            try {
                tablesRepository.createTable(account, table);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Void> updateTable(@NonNull Account account, @NonNull Table table) {
        return supplyAsync(() -> {
            try {
                tablesRepository.updateTable(account, table);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
}
