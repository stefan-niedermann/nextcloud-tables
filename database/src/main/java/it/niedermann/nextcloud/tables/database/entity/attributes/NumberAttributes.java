package it.niedermann.nextcloud.tables.database.entity.attributes;

import java.io.Serializable;

public record NumberAttributes(
        Double numberMin,
        Double numberMax,
        Integer numberDecimals,
        String numberPrefix,
        String numberSuffix
) implements Serializable {
}
