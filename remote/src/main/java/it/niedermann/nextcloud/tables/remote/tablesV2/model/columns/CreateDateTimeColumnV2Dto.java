package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;

import java.util.Objects;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;

public class CreateDateTimeColumnV2Dto extends CreateColumnV2Dto {

    private final String datetimeDefault;

    public CreateDateTimeColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto column) {
        super(tableRemoteId, column);
        this.datetimeDefault = column.datetimeDefault();
    }

    public String getDatetimeDefault() {
        return datetimeDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CreateDateTimeColumnV2Dto that = (CreateDateTimeColumnV2Dto) o;
        return Objects.equals(datetimeDefault, that.datetimeDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), datetimeDefault);
    }
}
