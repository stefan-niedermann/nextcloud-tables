package it.niedermann.nextcloud.tables.database.entity.attributes;

import androidx.annotation.Nullable;
import androidx.room.Ignore;

import java.io.Serializable;

public record NumberAttributes(
        @Nullable Double numberMin,
        @Nullable Double numberMax,
        @Nullable Integer numberDecimals,
        @Nullable String numberPrefix,
        @Nullable String numberSuffix
) implements Serializable {

    @Ignore
    public NumberAttributes() {
        this(null, null, null, null, null);
    }
}
