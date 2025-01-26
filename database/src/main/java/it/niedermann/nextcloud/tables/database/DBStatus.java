package it.niedermann.nextcloud.tables.database;

import androidx.annotation.Nullable;

public enum DBStatus {

    VOID(null),
    LOCAL_EDITED("LOCAL_EDITED"),
    LOCAL_DELETED("LOCAL_DELETED");

    @Nullable
    public final String title;

    DBStatus(@Nullable String title) {
        this.title = title;
    }
}
