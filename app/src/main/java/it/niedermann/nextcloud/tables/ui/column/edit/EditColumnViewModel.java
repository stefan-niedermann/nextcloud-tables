package it.niedermann.nextcloud.tables.ui.column.edit;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditColumnViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final TablesRepository tablesRepository;

    public EditColumnViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
        this.executor = Executors.newSingleThreadExecutor();
    }


    public CompletableFuture<Void> createColumn(@NonNull Account account, @NonNull Table table, @NonNull Column column) {
        return supplyAsync(() -> {
            try {
                tablesRepository.createColumn(account, table, column);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Void> updateColumn(@NonNull Account account, @NonNull Table table, @NonNull Column column) {
        return supplyAsync(() -> {
            try {
                tablesRepository.updateColumn(account, table, column);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

}
