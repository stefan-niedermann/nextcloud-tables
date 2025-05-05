package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import java.util.NoSuchElementException;

public enum EUserGroupTypeV2Dto {
    USER(0),
    GROUP(1),
    TEAMS(7),
    ;

    private final int remoteId;

    EUserGroupTypeV2Dto(int remoteId) {
        this.remoteId = remoteId;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public static EUserGroupTypeV2Dto findByRemoteId(int remoteId) {
        for (final var value : values()) {
            if (value.remoteId == remoteId) {
                return value;
            }
        }

        throw new NoSuchElementException("Could not find " + EUserGroupTypeV2Dto.class.getSimpleName() + " with remoteId " + remoteId);
    }
}
