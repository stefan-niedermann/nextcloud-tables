package it.niedermann.nextcloud.tables.ui.table.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
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

    public void createTable(@NonNull Account account, @NonNull Table table) {
        executor.submit(() -> {
            try {
                tablesRepository.createTable(account, table);
            } catch (NextcloudHttpRequestFailedException | IOException |
                     NextcloudFilesAppAccountNotFoundException e) {
                // TODO escalate?
                e.printStackTrace();
            }
        });
    }

    public void updateTable(@NonNull Table table) {
        executor.submit(() -> {
            try {
                tablesRepository.updateTable(table);
            } catch (NextcloudHttpRequestFailedException | IOException |
                     NextcloudFilesAppAccountNotFoundException e) {
                // TODO escalate?
                e.printStackTrace();
            }
        });
    }
}
