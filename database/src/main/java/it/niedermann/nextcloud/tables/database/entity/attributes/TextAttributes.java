package it.niedermann.nextcloud.tables.database.entity.attributes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Ignore;

import java.io.Serializable;

public record TextAttributes(
        @Nullable String textAllowedPattern,
        @Nullable Integer textMaxLength
) implements Serializable {

    @Ignore
    public TextAttributes() {
        this(null, null);
    }

    @Ignore
    public TextAttributes(@NonNull TextAttributes textAttributes) {
        this(textAttributes.textAllowedPattern, textAttributes.textMaxLength);
    }
}
