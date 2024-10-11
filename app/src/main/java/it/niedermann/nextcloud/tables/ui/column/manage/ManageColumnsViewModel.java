package it.niedermann.nextcloud.tables.ui.column.manage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class ManageColumnsViewModel extends AndroidViewModel {

    private final TablesRepository tablesRepository;

    public ManageColumnsViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
    }

    public LiveData<List<FullColumn>> getNotDeletedFullColumns$(@NonNull Table table) {
        return tablesRepository.getNotDeletedFullColumns$(table);
    }

    /**
     * @param columnIdOrder defines the desired column order
     */
    public CompletableFuture<Void> reorderColumns(@NonNull Account account, long tableId, @NonNull List<Long> columnIdOrder) {
        return tablesRepository.reorderColumn(account, tableId, columnIdOrder);
    }
}
