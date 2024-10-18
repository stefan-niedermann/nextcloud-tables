package it.niedermann.nextcloud.tables.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.repository.sync.SyncAdapter;

public abstract class AbstractRepository {

    protected final Context context;
    protected final TablesDatabase db;
    protected final SyncAdapter syncAdapter;
    protected final ExecutorService workExecutor;

    protected AbstractRepository(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.db = TablesDatabase.getInstance(this.context);
        this.syncAdapter = new SyncAdapter(this.context);
        this.workExecutor = ForkJoinPool.commonPool();
    }
}
