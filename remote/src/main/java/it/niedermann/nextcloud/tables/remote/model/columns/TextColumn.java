package it.niedermann.nextcloud.tables.remote.model.columns;

import androidx.annotation.NonNull;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;

public class TextColumn extends AbstractColumn {

    private final String textDefault;
    private final String textAllowedPattern;
    private final Integer textMaxLength;

    public TextColumn(long tableRemoteId, @NonNull Column column) {
        super(tableRemoteId, column);
        this.textDefault = column.getTextDefault();
        this.textAllowedPattern = column.getTextAllowedPattern();
        this.textMaxLength = column.getTextMaxLength();
    }

    public String getTextDefault() {
        return textDefault;
    }

    public String getTextAllowedPattern() {
        return textAllowedPattern;
    }

    public Integer getTextMaxLength() {
        return textMaxLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TextColumn that = (TextColumn) o;
        return Objects.equals(textDefault, that.textDefault) && Objects.equals(textAllowedPattern, that.textAllowedPattern) && Objects.equals(textMaxLength, that.textMaxLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), textDefault, textAllowedPattern, textMaxLength);
    }
}
