package it.niedermann.nextcloud.tables.shared;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public final class SharedExecutors {

    public static final ExecutorService CPU = ForkJoinPool.commonPool();
    public static final ExecutorService IO_NET = Executors.newFixedThreadPool(50);
    public static final ExecutorService IO_DB_INPUT = Executors.newSingleThreadExecutor();
    public static final ExecutorService IO_DB_OUTPUT = Executors.newCachedThreadPool();

}
