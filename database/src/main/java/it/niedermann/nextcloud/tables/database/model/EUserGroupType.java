package it.niedermann.nextcloud.tables.database.model;

public enum EUserGroupType {
    UNKNOWN(-1),
    USER(0),
    GROUP(1),
    CIRCLE(7),
    ;

    private final int remoteId;

    EUserGroupType(int remoteId) {
        this.remoteId = remoteId;
    }

    public static EUserGroupType findByRemoteId(int remoteId) {
        for (final var value : values()) {
            if (value.remoteId == remoteId) {
                return value;
            }
        }

        return UNKNOWN;
    }

    public int getRemoteId() {
        return remoteId;
    }
}
