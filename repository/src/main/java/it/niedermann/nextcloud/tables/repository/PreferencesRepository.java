package it.niedermann.nextcloud.tables.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import java.time.Instant;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceBooleanLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceLongLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceStringLiveData;

public class PreferencesRepository {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    public final String pref_key_sync_only_wifi;
    public final String pref_key_sync_background;
    public final String pref_key_sync_background_last;
    public final String pref_key_theme;
    private final LiveData<Boolean> syncOnlyOnWifi$;
    private final LiveData<Instant> lastBackgroundSync$;
    /**
     * @see AppCompatDelegate
     */
    private final LiveData<Integer> theme$;

    public PreferencesRepository(@NonNull Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = this.sharedPreferences.edit();

        this.pref_key_sync_only_wifi = context.getString(R.string.pref_key_sync_only_wifi);
        this.pref_key_sync_background = context.getString(R.string.pref_key_sync_background);
        this.pref_key_sync_background_last = context.getString(R.string.pref_key_sync_background_last);
        this.pref_key_theme = context.getString(R.string.pref_key_theme);

        syncOnlyOnWifi$ = new ReactiveLiveData<>(new SharedPreferenceBooleanLiveData(this.sharedPreferences, this.pref_key_sync_only_wifi, false))
                .distinctUntilChanged();
        lastBackgroundSync$ = new ReactiveLiveData<>(new SharedPreferenceLongLiveData(this.sharedPreferences, this.pref_key_sync_background_last, -1L))
                .distinctUntilChanged()
                .map(lastBackgroundSync -> lastBackgroundSync < 0 ? null : Instant.ofEpochMilli(lastBackgroundSync));
        theme$ = new ReactiveLiveData<>(new SharedPreferenceStringLiveData(this.sharedPreferences, this.pref_key_theme, String.valueOf(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)))
                .distinctUntilChanged()
                .map(Integer::parseInt);
    }

    public LiveData<Instant> getLastBackgroundSync$() {
        return lastBackgroundSync$;
    }

    public LiveData<Integer> getTheme$() {
        return theme$;
    }

    @WorkerThread
    public boolean syncOnlyOnWifi() {
        return this.sharedPreferences.getBoolean(this.pref_key_sync_only_wifi, true);
    }

    public LiveData<Boolean> syncOnlyOnWifi$() {
        return syncOnlyOnWifi$;
    }

    @WorkerThread
    public boolean isBackgroundSyncEnabled() {
        return this.sharedPreferences.getBoolean(this.pref_key_sync_background, true);
    }

    public void setLastBackgroundSync(@Nullable Instant instant) {
        if (instant == null) {
            this.editor.remove(this.pref_key_sync_background_last);
        } else {
            this.editor.putLong(this.pref_key_sync_background_last, instant.toEpochMilli());
        }
        this.editor.apply();
    }
}
