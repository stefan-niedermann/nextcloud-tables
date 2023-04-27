package it.niedermann.nextcloud.tables.ui.row;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditRowViewModel extends AndroidViewModel {

    private final TablesRepository tablesRepository;
    private final ExecutorService executor;

    public EditRowViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
        executor = Executors.newSingleThreadExecutor();
    }


    public LiveData<List<Column>> getNotDeletedColumns$(@NonNull Table table) {
        return tablesRepository.getNotDeletedColumns$(table);
    }

    public void createRow(@NonNull Account account, @NonNull Table table, @NonNull Collection<ColumnEditView> editors) {
        executor.submit(() -> {
            final var data = editors.stream().map(ColumnEditView::toData).toArray(Data[]::new);
            final var row = new Row();
            row.setCreatedBy(account.getUserName());
            row.setCreatedAt(Instant.now());
            row.setLastEditBy(account.getUserName());
            row.setLastEditAt(row.getCreatedAt());
            row.setTableId(table.getId());
            try {
                tablesRepository.createRow(account, row, data);
            } catch (NextcloudFilesAppAccountNotFoundException |
                     NextcloudHttpRequestFailedException | IOException e) {
                // TODO escalate?
                e.printStackTrace();
            }
        });
    }
}
