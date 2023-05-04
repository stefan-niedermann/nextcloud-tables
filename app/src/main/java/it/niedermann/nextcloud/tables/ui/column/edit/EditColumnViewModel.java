package it.niedermann.nextcloud.tables.ui.column.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditColumnViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final TablesRepository tablesRepository;

    public EditColumnViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
        this.executor = Executors.newSingleThreadExecutor();
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
