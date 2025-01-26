package it.niedermann.nextcloud.tables.remote.tablesV1.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.NoSuchElementException;

public enum EUserGroupTypeV1Dto {
    USER(0),
    GROUP(1),
    CIRCLE(7),
    ;

    @SerializedName("id")
    private final int remoteId;

    EUserGroupTypeV1Dto(int remoteId) {
        this.remoteId = remoteId;
    }

    public int getRemoteId() {
        return remoteId;
    }

    @NonNull
    public static EUserGroupTypeV1Dto findByRemoteId(int remoteId) {
        for (final var value : values()) {
            if (value.getRemoteId() == remoteId) {
                return value;
            }
        }

        throw new NoSuchElementException("Could not find " + EUserGroupTypeV1Dto.class.getSimpleName() + " with remoteId " + remoteId);
    }
}
