package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;

public enum ENodeTypeV2Dto {
    TABLE("table"),
    VIEW("view"),
    ;

    private final String nodeType;

    ENodeTypeV2Dto(@NonNull String nodeType) {
        this.nodeType = nodeType;
    }

    @NonNull
    @Override
    public String toString() {
        return this.nodeType;
    }
}
