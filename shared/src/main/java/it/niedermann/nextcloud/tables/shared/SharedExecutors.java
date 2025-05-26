package it.niedermann.nextcloud.tables.shared;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static it.niedermann.nextcloud.tables.shared.Constants.PROBABLE_ACCOUNT_COUNT;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class SharedExecutors {

    private static final int IO_NET_LIMIT_PER_HOST = 50;
    private static final PriorityThreadFactory PRIORITY_THREAD_FACTORY = new PriorityThreadFactory();

    private static final ExecutorService CPU = ForkJoinPool.commonPool();
    private static final ExecutorService IO_DB_READ_HIGH_PRIORITY = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MAX_PRIORITY));
    private static final ExecutorService IO_DB_WRITE_HIGH_PRIORITY = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MAX_PRIORITY));
    private static final ThreadPoolExecutor IO_DB_READ_LOW_PRIORITY = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MIN_PRIORITY));
    private static final ThreadPoolExecutor IO_DB_WRITE_LOW_PRIORITY = new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MIN_PRIORITY));
    private static final ExecutorService IO_NET_SHARED = newFixedThreadPool(2 * IO_NET_LIMIT_PER_HOST);
    private static final ConcurrentMap<String, ExecutorService> IO_NET_PER_HOST = new ConcurrentHashMap<>(PROBABLE_ACCOUNT_COUNT);

    static {
        IO_DB_READ_LOW_PRIORITY.allowCoreThreadTimeOut(true);
        IO_DB_WRITE_LOW_PRIORITY.allowCoreThreadTimeOut(true);
    }

    /// Only for non-blocking tasks
    @NonNull
    public static ExecutorService getCPUExecutor() {
        return CPU;
    }

    @NonNull
    public static ExecutorService getIoDbReadHighPriority() {
        return IO_DB_READ_HIGH_PRIORITY;
    }

    @NonNull
    public static ExecutorService getIoDbWriteHighPriority() {
        return IO_DB_WRITE_HIGH_PRIORITY;
    }

    public static ExecutorService getIoDbReadLowPriority() {
        return IO_DB_READ_LOW_PRIORITY;
    }

    public static ExecutorService getIoDbWriteLowPriority() {
        return IO_DB_WRITE_LOW_PRIORITY;
    }

    /// One shared thread pool executor instance for blocking network IO
    ///
    /// @see #getIONetExecutor(Uri)
    @NonNull
    public static ExecutorService getIoNetSharedExecutor() {
        return IO_NET_SHARED;
    }

    /// Fallback to [#getIoNetSharedExecutor()] in case `host` is `null`
    @NonNull
    public static ExecutorService getIONetExecutor(@Nullable Uri uri) {
        return Optional.ofNullable(uri)
                .map(Uri::getHost)
                .map(host -> IO_NET_PER_HOST.computeIfAbsent(host, ignored -> newFixedThreadPool(IO_NET_LIMIT_PER_HOST)))
                .orElseGet(SharedExecutors::getIoNetSharedExecutor);
    }

    private static class PriorityThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable r, int priority) {
            final var thread = Executors.defaultThreadFactory().newThread(r);
            thread.setPriority(priority);
            return thread;
        }

        @Override
        public Thread newThread(Runnable r) {
            return newThread(r, Thread.NORM_PRIORITY);
        }
    }
}
