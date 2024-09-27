package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;

import java.util.Objects;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;

public class CreateTextColumnV2Dto extends CreateColumnV2Dto {

    private final String textDefault;
    private final String textAllowedPattern;
    private final Integer textMaxLength;

    public CreateTextColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto column) {
        super(tableRemoteId, column);
        this.textDefault = column.textDefault();
        this.textAllowedPattern = column.textAllowedPattern();
        this.textMaxLength = column.textMaxLength();
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
        CreateTextColumnV2Dto that = (CreateTextColumnV2Dto) o;
        return Objects.equals(textDefault, that.textDefault) && Objects.equals(textAllowedPattern, that.textAllowedPattern) && Objects.equals(textMaxLength, that.textMaxLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), textDefault, textAllowedPattern, textMaxLength);
    }
}
