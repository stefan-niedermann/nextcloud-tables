package it.niedermann.nextcloud.tables.ui.column;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditColumnViewModel extends AndroidViewModel {

    private final TablesRepository tablesRepository;

    public EditColumnViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
    }


}
