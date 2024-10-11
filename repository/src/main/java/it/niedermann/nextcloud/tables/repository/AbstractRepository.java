package it.niedermann.nextcloud.tables.repository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractRepository {

    private static final ExecutorService DB_PARALLEL = Executors.newCachedThreadPool();
    private static final ExecutorService DB_SEQUENTIAL = Executors.newSingleThreadExecutor();
    private static final ExecutorService SYNC = Executors.newSingleThreadExecutor();

    protected final ExecutorService dbParallelExecutor;
    protected final ExecutorService dbSequentialExecutor;
    protected final ExecutorService syncExecutor;

    protected AbstractRepository() {
        this(
                DB_PARALLEL,
                DB_SEQUENTIAL,
                SYNC
        );
    }

    private AbstractRepository(
            final ExecutorService dbParallelExecutor,
            final ExecutorService dbSequentialExecutor,
            final ExecutorService syncExecutor
    ) {
        this.dbParallelExecutor = dbParallelExecutor;
        this.dbSequentialExecutor = dbSequentialExecutor;
        this.syncExecutor = syncExecutor;
    }
}
