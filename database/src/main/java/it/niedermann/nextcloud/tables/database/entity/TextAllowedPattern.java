package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.Objects;

@Entity(
        inheritSuperIndices = true
)
public class TextAllowedPattern extends AbstractEntity {

    private String pattern;

    public TextAllowedPattern() {
        // Default constructor
    }

    @Ignore
    public TextAllowedPattern(@NonNull TextAllowedPattern textAllowedPattern) {
        super(textAllowedPattern);
        this.pattern = textAllowedPattern.pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TextAllowedPattern that = (TextAllowedPattern) o;
        return Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pattern);
    }
}
