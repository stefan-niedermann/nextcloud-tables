package it.niedermann.nextcloud.tables.remote.exception;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.model.EPermission;

public class InsufficientPermissionException extends Exception {

    private final EPermission missingPermission;

    public InsufficientPermissionException(@NonNull EPermission missingPermission) {
        this.missingPermission = missingPermission;
    }

    @NonNull
    public EPermission getMissingPermission() {
        return missingPermission;
    }

    @Nullable
    @Override
    public String getMessage() {
        return String.valueOf(missingPermission);
    }
}
