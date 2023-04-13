package it.niedermann.nextcloud.tables.ui.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@GlideModule
public class CustomAppGlideModule extends AppGlideModule {

    private static final String TAG = CustomAppGlideModule.class.getSimpleName();
    private static final ExecutorService clearDiskCacheExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }

    @UiThread
    public static void clearCache(@NonNull Context context) {
        final var cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            final var activeNetworkInfo = cm.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                Log.i(TAG, "Clearing Glide memory cache");
                Glide.get(context).clearMemory();
                clearDiskCacheExecutor.submit(() -> {
                    Log.i(TAG, "Clearing Glide disk cache");
                    Glide.get(context.getApplicationContext()).clearDiskCache();
                });
            } else {
                Log.i(TAG, "Do not clear Glide caches, because the user currently does not have a working internet connection");
            }
        } else {
            Log.w(TAG, ConnectivityManager.class.getSimpleName() + " is null");
        }
    }
}