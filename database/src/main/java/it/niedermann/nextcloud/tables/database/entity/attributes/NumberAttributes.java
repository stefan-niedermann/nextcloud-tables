package it.niedermann.nextcloud.tables.database.entity.attributes;

import androidx.room.Ignore;

import java.io.Serializable;

public record NumberAttributes(
        Double numberMin,
        Double numberMax,
        Integer numberDecimals,
        String numberPrefix,
        String numberSuffix
) implements Serializable {

    @Ignore
    public NumberAttributes() {
        this(null, null, null, null, null);
    }
}
