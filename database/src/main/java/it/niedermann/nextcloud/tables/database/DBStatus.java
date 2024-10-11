package it.niedermann.nextcloud.tables.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.NoSuchElementException;

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

    public static DBStatus findByTitle(@Nullable String title) {
        for (DBStatus status : DBStatus.values()) {
            if (status.getTitle().equals(title)) {
                return status;
            }
        }

        if (BuildConfig.DEBUG) {
            throw new NoSuchElementException("Can not find " + DBStatus.class.getSimpleName() + " with title " + title);
        }

        return DBStatus.VOID;
    }
}
