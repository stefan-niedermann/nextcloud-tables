package it.niedermann.nextcloud.tables.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import it.niedermann.nextcloud.tables.repository.exception.ServerNotAvailableException;
import retrofit2.Response;

public class ServerErrorHandler {

    private static final String TAG = ServerErrorHandler.class.getSimpleName();
    private final Context context;

    public ServerErrorHandler(@NonNull Context context) {
        this.context = context;
    }

    public void handle(@NonNull Response<?> response) throws Exception {
        handle(response, "", Strategy.THROW_ALWAYS_EXCEPT_NOT_MODIFIED);
    }

    public void handle(@NonNull Response<?> response,
                       @NonNull String message) throws Exception {
        handle(response, message, Strategy.THROW_ALWAYS_EXCEPT_NOT_MODIFIED);
    }

    public void handle(@NonNull Response<?> response,
                       @NonNull String message,
                       @NonNull Strategy strategy) throws Exception {
        final var exception = responseToException(response, message, strategy == Strategy.THROW_ALWAYS_EXCEPT_NOT_MODIFIED);
        if (exception.isPresent()) {
            throw exception.get();
        }
    }

    @NonNull
    public Optional<Exception> responseToException(@NonNull Response<?> response,
                                                   @NonNull String message,
                                                   boolean tolerateNotModified) {
        if (response.isSuccessful()) {
            throw new IllegalStateException("The " + Response.class.getSimpleName() + " was successful. This must be handled before passing the " + Response.class.getSimpleName() + " to this method.");
        }

        final var msg = extractErrorBody(response)
                .map(error -> message + "\n" + error)
                .orElse(message);

        return switch (response.code()) {
            case 304 -> {
                if (tolerateNotModified) {
                    Log.i(TAG, "HTTP " + response.code() + " Not Modified");
                    yield Optional.empty();

                } else {
                    yield Optional.of(new NextcloudHttpRequestFailedException(context, response.code(), new RuntimeException(msg)));
                }
            }
            case 500 ->
                    Optional.of(new ServerNotAvailableException(ServerNotAvailableException.Reason.SERVER_ERROR, msg));
            case 503 ->
                    Optional.of(new ServerNotAvailableException(ServerNotAvailableException.Reason.MAINTENANCE_MODE, msg));
            case 520 -> {
                for (final var handler : Handler.values()) {
                    if (handler.canHandle(response.message())) {
                        yield Optional.of(handler.exception);
                    }
                }

                yield Optional.of(new NextcloudHttpRequestFailedException(context, response.code(), new RuntimeException(msg)));
            }
            default ->
                    Optional.of(new NextcloudHttpRequestFailedException(context, response.code(), new RuntimeException(msg)));
        };
    }

    @NonNull
    private Optional<String> extractErrorBody(@NonNull Response<?> response) {
        try (var errorBody = response.errorBody()) {
            return Optional.ofNullable(errorBody).map(responseBody -> {
                try {
                    return responseBody.string();
                } catch (IOException e) {
                    return null;
                }
            });
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public enum Strategy {
        /**
         * Will throw an {@link Exception} in any case. Caller is responsible for checking acceptable status codes
         */
        THROW_ALWAYS_EXCEPT_NOT_MODIFIED,
        /**
         * Will only throw an {@link Exception} if a known error occurs and do nothing otherwise
         */
        THROW_ON_KNOWN_ERROR_EXCEPT_NOT_MODIFIED
    }

    private enum Handler {
        DEVICE_OFFLINE(
                List.of("econnrefused", "unable to resolve host", "connection refused", "no address associated with hostname"),
                new ServerNotAvailableException(ServerNotAvailableException.Reason.DEVICE_OFFLINE)
        ),
        CONNECTION_TIMEOUT(
                Collections.singletonList("connecttimeoutexception"),
                new SocketTimeoutException()
        ),
        ;

        private final Collection<String> indicators;
        private final Exception exception;

        Handler(@NonNull Collection<String> indicators, @NonNull Exception exception) {
            this.indicators = indicators;
            this.exception = exception;
        }

        boolean canHandle(@Nullable String message) {
            if (message == null) {
                return false;
            }
            final var lower = message.toLowerCase();
            return indicators.stream().anyMatch(lower::contains);
        }
    }
}
