package it.niedermann.nextcloud.tables.database.model;

public enum EUserGroupType {
    UNKNOWN(-1),
    USER(0),
    GROUP(1),
    CIRCLE(7),
    ;

    private final int remoteType;

    EUserGroupType(int remoteType) {
        this.remoteType = remoteType;
    }

    public static EUserGroupType findByRemoteId(int remoteType) {
        for (final var value : values()) {
            if (value.remoteType == remoteType) {
                return value;
            }
        }

        return UNKNOWN;
    }

    public int getRemoteType() {
        return remoteType;
    }
}
