package it.niedermann.nextcloud.tables.repository.exception;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.EPermissionV2Dto;

public class InsufficientPermissionException extends Exception {

    private final EPermissionV2Dto missingPermission;

    public InsufficientPermissionException(@NonNull EPermissionV2Dto missingPermission) {
        this.missingPermission = missingPermission;
    }

    @NonNull
    public EPermissionV2Dto getMissingPermission() {
        return missingPermission;
    }

    @Nullable
    @Override
    public String getMessage() {
        return String.valueOf(missingPermission);
    }
}
