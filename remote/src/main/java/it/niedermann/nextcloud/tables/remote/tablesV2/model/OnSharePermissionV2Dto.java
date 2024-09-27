package it.niedermann.nextcloud.tables.remote.tablesV2.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public record OnSharePermissionV2Dto(
        @Nullable Boolean read,
        @Nullable Boolean create,
        @Nullable Boolean update,
        @Nullable Boolean delete,
        @Nullable Boolean manage
) implements Serializable {
}
