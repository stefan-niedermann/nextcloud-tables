package it.niedermann.nextcloud.tables.remote.exception;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import it.niedermann.nextcloud.tables.R;

public class ServerNotAvailableException extends Exception {

    private final Reason reason;

    public ServerNotAvailableException() {
        this(Reason.UNKNOWN);
    }

    public ServerNotAvailableException(@NonNull Reason reason) {
        this(reason, null);
    }

    public ServerNotAvailableException(@NonNull Reason reason, @Nullable String message) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }

    @Nullable
    @Override
    public String getMessage() {
        final var message = super.getMessage();
        if (TextUtils.isEmpty(message)) {
            return String.valueOf(getReason());
        }
        return String.format("%s: %s", getReason(), message);
    }

    public enum Reason {
        NOT_INSTALLED(R.string.reason_not_installed),
        NOT_ENABLED(R.string.reason_not_enabled),
        MAINTENANCE_MODE(R.string.reason_maintenance_mode),
        SERVER_ERROR(R.string.reason_server_error),
        DEVICE_OFFLINE(R.string.reason_offline),
        TABLES_NOT_SUPPORTED(R.string.reason_tables_not_supported),
        NEXTCLOUD_NOT_SUPPORTED(R.string.reason_nextcloud_not_supported),
        UNKNOWN(R.string.reason_unknown),
        ;

        @StringRes
        public final int messageRes;

        Reason(@StringRes int messageRes) {
            this.messageRes = messageRes;
        }
    }
}
