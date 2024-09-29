package it.niedermann.nextcloud.tables.remote.tablesV2.model;

public enum EUserGroupTypeV2Dto {
    USER(0),
    GROUP(1),
    CIRCLE(7),
    ;

    private final int remoteId;

    EUserGroupTypeV2Dto(int remoteId) {
        this.remoteId = remoteId;
    }

    public int getRemoteId() {
        return remoteId;
    }
}
