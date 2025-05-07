package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.NoSuchElementException;

public enum ENodeTypeV2Dto {
    TABLE("table", 0),
    VIEW("view", 1),
    ;

    public final String nodeType;
    public final int id;

    ENodeTypeV2Dto(@NonNull String nodeType, int id) {
        this.nodeType = nodeType;
        this.id = id;
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
