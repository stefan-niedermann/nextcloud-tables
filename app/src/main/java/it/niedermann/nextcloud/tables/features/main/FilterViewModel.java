package it.niedermann.nextcloud.tables.features.main;

import android.app.Application;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.model.FilterConstraints;

@MainThread
public class FilterViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final LiveData<FilterConstraints> filterConstraints;

    public FilterViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        this.filterConstraints = savedStateHandle.getLiveData("constraints", null);
    }

    public void setTerm(@Nullable String term) {
        savedStateHandle.set("constraints", new FilterConstraints(Optional.ofNullable(term).orElse("")));
    }

    public LiveData<FilterConstraints> getFilterConstraints() {
        return filterConstraints;
    }
}
