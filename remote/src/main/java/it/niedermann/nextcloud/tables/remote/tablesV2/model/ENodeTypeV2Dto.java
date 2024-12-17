package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.NoSuchElementException;

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

    public static ENodeTypeV2Dto findByString(@Nullable String stringRepresentation) {
        for (final var value : values()) {
            if (value.nodeType.equals(stringRepresentation)) {
                return value;
            }
        }

        throw new NoSuchElementException("Unknown " + ENodeTypeV2Dto.class.getSimpleName() + ": \"" + stringRepresentation + "\"");
    }
}
