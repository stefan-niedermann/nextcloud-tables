package it.niedermann.nextcloud.tables.database.entity.attributes;

import androidx.room.Ignore;

import java.io.Serializable;

public record TextAttributes(
        String textAllowedPattern,
        Integer textMaxLength
) implements Serializable {

    @Ignore
    public TextAttributes() {
        this(null, null);
    }
}
