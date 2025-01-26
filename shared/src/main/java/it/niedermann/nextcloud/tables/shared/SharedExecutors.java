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

public final class SharedExecutors {

    private static final int IO_NET_LIMIT_PER_HOST = 50;

    private static final ExecutorService CPU = ForkJoinPool.commonPool();
    private static final ExecutorService IO_DB_INPUT = Executors.newSingleThreadExecutor();
    private static final ExecutorService IO_DB_OUTPUT = Executors.newCachedThreadPool();
    private static final ExecutorService IO_NET_SHARED = newFixedThreadPool(2 * IO_NET_LIMIT_PER_HOST);

    private static final ConcurrentMap<String, ExecutorService> IO_NET_PER_HOST = new ConcurrentHashMap<>(PROBABLE_ACCOUNT_COUNT);

    /// Only for non-blocking tasks
    @NonNull
    public static ExecutorService getCPUExecutor() {
        return CPU;
    }

    /// @see #getIODbOutputExecutor()
    @NonNull
    public static ExecutorService getIODbInputExecutor() {
        return IO_DB_INPUT;
    }

    /// @see #getIODbInputExecutor()
    @NonNull
    public static ExecutorService getIODbOutputExecutor() {
        return IO_DB_OUTPUT;
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
}
