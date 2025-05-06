package it.niedermann.nextcloud.tables.features.about;

import android.app.Application;
import android.text.format.DateUtils;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collection;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.PreferencesRepository;

@MainThread
public class AboutViewModel extends AndroidViewModel {

    private final AccountRepository accountRepository;
    private final PreferencesRepository preferencesRepository;

    private final LiveData<String> appVersion$;
    private final LiveData<Collection<Pair<String, TablesVersion>>> tablesServerVersion$;

    public AboutViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
        this.preferencesRepository = new PreferencesRepository(application);

        appVersion$ = new MutableLiveData<>(BuildConfig.VERSION_NAME);
        tablesServerVersion$ = new ReactiveLiveData<>(this.accountRepository.getTablesServerVersion())
                .distinctUntilChanged()
                .map(serverVersions -> serverVersions
                        .entrySet()
                        .stream()
                        .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                        .toList());
    }

    @NonNull
    public LiveData<Collection<Pair<String, TablesVersion>>> getTablesServerVersion() {
        return tablesServerVersion$;
    }

    @NonNull
    public LiveData<String> getAppVersion() {
        return appVersion$;
    }

    @NonNull
    public LiveData<CharSequence> getLastBackgroundSync() {
        return new ReactiveLiveData<>(preferencesRepository.getLastBackgroundSync$())
                .map(lastBackgroundSync -> {
                    if (lastBackgroundSync == null) {
                        return getApplication().getString(R.string.simple_disabled);
                    } else {
                        return DateUtils.getRelativeTimeSpanString(lastBackgroundSync.toEpochMilli());
                    }
                });
    }

}
