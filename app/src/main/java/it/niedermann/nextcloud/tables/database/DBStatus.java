package it.niedermann.nextcloud.tables.database;

import androidx.annotation.NonNull;

public enum DBStatus {

    VOID(""),
    LOCAL_EDITED("LOCAL_EDITED"),
    LOCAL_DELETED("LOCAL_DELETED");

    @NonNull
    private final String title;

    @NonNull
    public String getTitle() {
        return title;
    }

    DBStatus(@NonNull String title) {
        this.title = title;
    }
}
