package it.niedermann.nextcloud.tables.ui.column.edit;

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

@MainThread
public class EditColumnViewModel extends AndroidViewModel {

    private final TablesRepository tablesRepository;

    public EditColumnViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
    }

    public CompletableFuture<Void> createColumn(@NonNull Account account, @NonNull Table table, @NonNull Column column) {
        return tablesRepository.createColumn(account, table, column);
    }

    public CompletableFuture<Void> updateColumn(@NonNull Account account, @NonNull Table table, @NonNull Column column) {
        return tablesRepository.updateColumn(account, table, column);
    }

}
