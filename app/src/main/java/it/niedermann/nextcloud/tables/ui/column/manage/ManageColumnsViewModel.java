package it.niedermann.nextcloud.tables.ui.column.manage;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class ManageColumnsViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final TablesRepository tablesRepository;

    public ManageColumnsViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Column>> getNotDeletedColumns$(@NonNull Table table) {
        return tablesRepository.getNotDeletedColumns$(table);
    }

    public void createColumn(@NonNull Account account, @NonNull Column column) {
        executor.submit(() -> {
            try {
                tablesRepository.createColumn(account, column);
            } catch (NextcloudHttpRequestFailedException | IOException |
                     NextcloudFilesAppAccountNotFoundException e) {
                // TODO escalate?
                e.printStackTrace();
            }
        });
    }

    public void updateColumn(@NonNull Account account, @NonNull Column column) {
        executor.submit(() -> {
            try {
                tablesRepository.updateColumn(account, column);
            } catch (NextcloudHttpRequestFailedException | IOException |
                     NextcloudFilesAppAccountNotFoundException e) {
                // TODO escalate?
                e.printStackTrace();
            }
        });
    }
}
