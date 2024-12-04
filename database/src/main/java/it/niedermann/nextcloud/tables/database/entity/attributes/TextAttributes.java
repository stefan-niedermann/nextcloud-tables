package it.niedermann.nextcloud.tables.database.entity.attributes;

public record TextAttributes(
        String textAllowedPattern,
        Integer textMaxLength
) {
}
