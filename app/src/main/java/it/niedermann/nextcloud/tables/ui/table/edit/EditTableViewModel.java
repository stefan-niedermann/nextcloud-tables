package it.niedermann.nextcloud.tables.ui.table.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditTableViewModel extends AndroidViewModel {

    private final TablesRepository tablesRepository;

    public EditTableViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
    }

    public CompletableFuture<Void> createTable(@NonNull Account account,
                                               @NonNull Table table) {
        return tablesRepository.createTable(account, table);
    }

    public CompletableFuture<Void> updateTable(@NonNull Account account,
                                               @NonNull Table table) {
        return tablesRepository.updateTable(account, table);
    }
}
