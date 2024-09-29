package it.niedermann.nextcloud.tables.remote.tablesV1.model;

public enum EUserGroupTypeV1Dto {
    USER(0),
    GROUP(1),
    CIRCLE(7),
    ;

    private final int remoteId;

    EUserGroupTypeV1Dto(int remoteId) {
        this.remoteId = remoteId;
    }

    public int getRemoteId() {
        return remoteId;
    }
}
