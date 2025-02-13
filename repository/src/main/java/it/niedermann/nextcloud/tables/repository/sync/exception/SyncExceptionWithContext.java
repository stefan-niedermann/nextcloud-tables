package it.niedermann.nextcloud.tables.repository.sync.exception;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletionException;

public abstract class SyncExceptionWithContext extends CompletionException {

    public SyncExceptionWithContext(@Nullable Throwable cause) {
        super(cause);
    }

    @Nullable
    @Override
    public String getMessage() {
        final var sb = new StringBuilder("\n")
                .append("\n")
                .append("\uD83D\uDEA8  Remove sensitive data before pasting into issues:\n")
                .append("\n")
                .append("┌── Start personal data ────────────────────────────────────────────────────────\n")
                .append("│\n");

        for (final var entry : getAttributes().entrySet()) {
            sb.append("│   ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return sb
                .append("│\n")
                .append("└── End personal data ──────────────────────────────────────────────────────────\n")
                .append("\n")
                .toString();
    }

    @NonNull
    public abstract Map<String, Object> getAttributes();

    /// @noinspection UnusedReturnValue
    /// because it is thrown
    @NonNull
    public abstract SyncExceptionWithContext provide(@Nullable Object value);

    /// @see #provide(Object)
    @NonNull
    public final SyncExceptionWithContext provide(@Nullable Object... values) {
        if (values != null) {
            Arrays.stream(values).forEach(this::provide);
        }

        return this;
    }

    @NonNull
    @Override
    public StackTraceElement[] getStackTrace() {
        return new StackTraceElement[]{};
    }

    @NonNull
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
