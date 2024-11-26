package it.niedermann.nextcloud.tables;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import it.niedermann.nextcloud.tables.repository.PreferencesRepository;
import it.niedermann.nextcloud.tables.shared.config.FeatureToggle;
import it.niedermann.nextcloud.tables.util.CustomAppGlideModule;

public class TablesApplication extends Application {

    private static final String TAG = TablesApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        final var repo = new PreferencesRepository(this);

        if (FeatureToggle.STRICT_MODE.enabled) {
            enableStrictModeLogging();
        }

        repo.getTheme$().observeForever(AppCompatDelegate::setDefaultNightMode);

        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "--- Low memory: Clear Glide cache ---");
        CustomAppGlideModule.clearCache(this);
        Log.w(TAG, "--- Low memory: Clear debug log ---");
    }

    private void enableStrictModeLogging() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }
}
