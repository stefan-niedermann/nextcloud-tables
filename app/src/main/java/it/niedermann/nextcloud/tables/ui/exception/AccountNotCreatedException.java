package it.niedermann.nextcloud.tables.ui.exception;

import androidx.annotation.Nullable;

public class AccountNotCreatedException extends RuntimeException {

    public AccountNotCreatedException() {
        super();
    }

    public AccountNotCreatedException(@Nullable Throwable cause) {
        super(cause);
    }
}
