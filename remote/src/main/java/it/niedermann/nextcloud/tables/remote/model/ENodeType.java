package it.niedermann.nextcloud.tables.remote.model;

import androidx.annotation.NonNull;

public enum ENodeType {
    TABLE("table"),
    VIEW("view"),
    ;

    private final String nodeType;

    ENodeType(@NonNull String nodeType) {
        this.nodeType = nodeType;
    }

    @NonNull
    @Override
    public String toString() {
        return this.nodeType;
    }
}
