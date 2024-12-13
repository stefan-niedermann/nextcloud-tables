package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;

import java.io.Serializable;

import it.niedermann.nextcloud.tables.database.DBStatus;

public record SynchronizationContext(
        @Nullable DBStatus status,
        @Nullable String eTag
) implements Serializable {

    @Ignore
    public SynchronizationContext() {
        this(DBStatus.VOID, null);
    }

    @Ignore
    public SynchronizationContext(@NonNull SynchronizationContext remoteContext) {
        this(remoteContext.status, remoteContext.eTag);
    }
}
