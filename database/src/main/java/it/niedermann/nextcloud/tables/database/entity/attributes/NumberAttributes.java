package it.niedermann.nextcloud.tables.database.entity.attributes;

public record NumberAttributes(
        Double numberMin,
        Double numberMax,
        Integer numberDecimals,
        String numberPrefix,
        String numberSuffix
) {
}
