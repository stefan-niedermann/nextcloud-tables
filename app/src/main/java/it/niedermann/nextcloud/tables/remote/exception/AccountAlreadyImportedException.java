package it.niedermann.nextcloud.tables.remote.exception;

import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;

public class AccountAlreadyImportedException extends RuntimeException {

    public AccountAlreadyImportedException(@NonNull SQLiteConstraintException cause) {
        super(cause);
    }
}
