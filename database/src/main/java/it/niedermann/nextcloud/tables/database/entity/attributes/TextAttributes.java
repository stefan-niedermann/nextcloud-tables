package it.niedermann.nextcloud.tables.database.entity.attributes;

import java.io.Serializable;

public record TextAttributes(
        String textAllowedPattern,
        Integer textMaxLength
) implements Serializable {
}
