package it.niedermann.nextcloud.tables.repository.exception;

import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;

public class AccountAlreadyImportedException extends AccountNotCreatedException {

    public AccountAlreadyImportedException(@NonNull SQLiteConstraintException cause) {
        super(cause);
    }
}
