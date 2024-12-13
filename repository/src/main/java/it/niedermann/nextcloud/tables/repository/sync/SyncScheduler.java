package it.niedermann.nextcloud.tables.repository.sync;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.sync.paralleltreesync.ParallelTreeSyncAdapter;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;

public interface SyncScheduler {

    @AnyThread
    CompletableFuture<Void> scheduleSynchronization(@NonNull Account account,
                                                    @Nullable SyncStatusReporter reporter);

    class Factory {
        @NonNull
        public SyncScheduler create(@NonNull Context context) {
            return new ParallelTreeSyncAdapter(context);
        }
    }
}
