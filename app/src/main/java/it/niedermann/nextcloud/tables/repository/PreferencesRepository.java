package it.niedermann.nextcloud.tables.repository;

import static androidx.lifecycle.Transformations.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import java.time.Instant;

import it.niedermann.android.sharedpreferences.SharedPreferenceBooleanLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceLongLiveData;
import it.niedermann.android.sharedpreferences.SharedPreferenceStringLiveData;
import it.niedermann.nextcloud.tables.R;

public class PreferencesRepository {

    private static final String TAG = PreferencesRepository.class.getSimpleName();
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    public final String pref_key_sync_only_wifi;
    public final String pref_key_sync_background;
    public final String pref_key_sync_background_last;
    public final String pref_key_theme;
    public final LiveData<Boolean> syncOnlyOnWifi$;
    public final LiveData<Instant> lastBackgroundSync$;
    /**
     * @see AppCompatDelegate
     */
    public final LiveData<Integer> theme$;

    public PreferencesRepository(@NonNull Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = this.sharedPreferences.edit();

        this.pref_key_sync_only_wifi = context.getString(R.string.pref_key_sync_only_wifi);
        this.pref_key_sync_background = context.getString(R.string.pref_key_sync_background);
        this.pref_key_sync_background_last = context.getString(R.string.pref_key_sync_background_last);
        this.pref_key_theme = context.getString(R.string.pref_key_theme);

        syncOnlyOnWifi$ = new SharedPreferenceBooleanLiveData(this.sharedPreferences, this.pref_key_sync_only_wifi, false);
        lastBackgroundSync$ = map(
                new SharedPreferenceLongLiveData(this.sharedPreferences, this.pref_key_sync_background_last, -1L),
                lastBackgroundSync -> lastBackgroundSync < 0 ? null : Instant.ofEpochMilli(lastBackgroundSync)
        );
        theme$ = map(
                new SharedPreferenceStringLiveData(this.sharedPreferences, this.pref_key_theme, String.valueOf(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)),
                Integer::parseInt
        );
    }

    public LiveData<NetworkRequest> getNetworkRequest$() {
        return map(this.syncOnlyOnWifi$, syncOnlyOnWifi -> {
            final var networkRequestBuilder = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                    .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET);

            if (!syncOnlyOnWifi) {
                networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
            }

            return networkRequestBuilder.build();
        });
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
