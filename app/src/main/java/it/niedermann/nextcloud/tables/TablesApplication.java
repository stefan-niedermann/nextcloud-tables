package it.niedermann.nextcloud.tables;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.PreferencesRepository;
import it.niedermann.nextcloud.tables.repository.sync.treesync.TreeSyncScheduler;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;
import it.niedermann.nextcloud.tables.shared.SharedExecutors;
import it.niedermann.nextcloud.tables.util.CustomAppGlideModule;

public class TablesApplication extends Application {

    private static final Logger logger = Logger.getLogger(TreeSyncScheduler.class.getSimpleName());

    private final ExecutorService workExecutor = SharedExecutors.getCPUExecutor();

    @Override
    public void onCreate() {
        final var preferencesRepository = new PreferencesRepository(this);
        final var accountRepository = new AccountRepository(this);

        if (FeatureToggle.STRICT_MODE.enabled) {
            enableStrictModeLogging();
        }

        preferencesRepository.getTheme$().observeForever(AppCompatDelegate::setDefaultNightMode);


        final var connectivityManager = getSystemService(ConnectivityManager.class);
        final var networkCallbackReference = new AtomicReference<ConnectivityManager.NetworkCallback>();

        final var networkRequest$ = new ReactiveLiveData<>(preferencesRepository.syncOnlyOnWifi$())
                .map(syncOnlyOnWifi -> {
                    final var networkRequestBuilder = new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
                            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET);

                    if (!syncOnlyOnWifi) {
                        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
                    }

                    return networkRequestBuilder.build();
                }, workExecutor);

        new ReactiveLiveData<>(accountRepository.getCurrentAccount())
                .filter(Objects::nonNull)
                .combineWith(() -> networkRequest$)
                .observeForever(args -> workExecutor.execute(() -> {
                    final var account = args.first;
                    final var networkRequest = args.second;

                    networkCallbackReference.getAndUpdate(ref -> {
                        if (ref != null) {
                            connectivityManager.unregisterNetworkCallback(ref);
                        }

                        final var newNetworkCallback = new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(@NonNull Network network) {
                                super.onAvailable(network);

                                logger.info("Network available, trigger synchronization for " + account);

                                accountRepository.scheduleSynchronization(account).whenCompleteAsync((result, exception) -> {
                                    if (exception != null) {
                                        logger.log(Level.SEVERE, exception.toString(), exception);
                                    }
                                });
                            }
                        };

                        connectivityManager.requestNetwork(networkRequest, newNetworkCallback);

                        return newNetworkCallback;
                    });
                }));

        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        logger.warning("--- Low memory: Clear Glide cache ---");
        CustomAppGlideModule.clearCache(this);
        logger.warning("--- Low memory: Clear debug log ---");
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
