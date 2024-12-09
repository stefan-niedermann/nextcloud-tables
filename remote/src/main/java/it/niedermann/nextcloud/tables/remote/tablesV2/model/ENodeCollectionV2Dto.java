package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;

public enum ENodeCollectionV2Dto {
    TABLES("tables"),
    ;

    private final String nodeCollection;

    ENodeCollectionV2Dto(@NonNull String nodeCollection) {
        this.nodeCollection = nodeCollection;
    }

    @NonNull
    @Override
    public String toString() {
        return this.nodeCollection;
    }
}
