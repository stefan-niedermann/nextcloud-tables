package it.niedermann.nextcloud.tables;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import it.niedermann.nextcloud.tables.repository.PreferencesRepository;
import it.niedermann.nextcloud.tables.ui.util.CustomAppGlideModule;

public class TablesApplication extends Application {

    private static final String TAG = TablesApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        final var repo = new PreferencesRepository(this);

        if (BuildConfig.DEBUG) {
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

    public enum FeatureToggles {
        EDIT_COLUMN(BuildConfig.DEBUG),
        CREATE_COLUMN(BuildConfig.DEBUG),
        DELETE_COLUMN(BuildConfig.DEBUG),
        SHARE_TABLE(BuildConfig.DEBUG),
        SEARCH_IN_TABLE(false),
        RICH_EDITOR(true),
        ;

        public final boolean enabled;

        FeatureToggles(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
