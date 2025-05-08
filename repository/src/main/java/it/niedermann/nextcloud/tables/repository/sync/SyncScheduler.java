package it.niedermann.nextcloud.tables.repository.sync;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;
import it.niedermann.nextcloud.tables.repository.sync.treesync.TreeSyncScheduler;

public interface SyncScheduler {

    @AnyThread
    CompletableFuture<Void> scheduleSynchronization(@NonNull Account account,
                                                    @NonNull Scope scope,
                                                    @Nullable SyncStatusReporter reporter);

    class Factory {

        private final SyncScheduler defaultSyncScheduler;

        public Factory(@NonNull Context context) {
            defaultSyncScheduler = new TreeSyncScheduler(context.getApplicationContext());
        }

        @NonNull
        public SyncScheduler create() {
            return defaultSyncScheduler;
        }
    }

    enum Scope {
        PUSH_ONLY,
        PUSH_AND_PULL
    }
}
