package it.niedermann.nextcloud.tables.repository.sync.treesync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import it.niedermann.nextcloud.tables.repository.sync.exception.SyncExceptionWithContext;

public class TreeSyncExceptionWithContext extends SyncExceptionWithContext {

    @NonNull
    private final Map<String, Serializable> attributes = new HashMap<>();

    /// @implNote Avoids duplicated [SyncExceptionWithContext] instances by incorporating already
    /// existing contextual information and dropping the top `cause` from the cause chain in case it
    /// is an [SyncExceptionWithContext].
    public TreeSyncExceptionWithContext(@Nullable Throwable cause) {
        super(cause instanceof SyncExceptionWithContext ? cause.getCause() : cause);

        if (cause instanceof SyncExceptionWithContext syncExceptionWithContext) {
            provide(syncExceptionWithContext.getAttributes().values().toArray());
        }
    }

    @NonNull
    @Override
    public Map<String, Serializable> getAttributes() {
        return attributes;
    }

    /// Assuming that the cause must have more accurate contextual information than the instance to create
    ///
    /// @implNote Provided value(s) will only be accepted if there is not already an existing value of the values class present.
    @Override
    public @NonNull SyncExceptionWithContext provide(@Nullable Serializable value) {
        if (value != null) {
            return provide(value.getClass().getSimpleName(), value);
        }

        return this;
    }

    /// @see #provide(Serializable)
    public @NonNull SyncExceptionWithContext provide(@NonNull String key, @Nullable Serializable value) {
        if (value != null) {
            attributes.putIfAbsent(key, value);
        }

        return this;
    }
}
