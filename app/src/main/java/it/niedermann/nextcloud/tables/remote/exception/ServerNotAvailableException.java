package it.niedermann.nextcloud.tables.remote.exception;

import androidx.annotation.NonNull;

public class ServerNotAvailableException extends Exception {

    private final Reason reason;

    public ServerNotAvailableException() {
        this(Reason.UNKNOWN);
    }

    public ServerNotAvailableException(@NonNull Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }

    public enum Reason {
        NOT_INSTALLED,
        NOT_ENABLED,
        MAINTENANCE_MODE,
        SERVER_ERROR,
        TABLES_NOT_SUPPORTED,
        NEXTCLOUD_NOT_SUPPORTED,
        UNKNOWN
    }
}
