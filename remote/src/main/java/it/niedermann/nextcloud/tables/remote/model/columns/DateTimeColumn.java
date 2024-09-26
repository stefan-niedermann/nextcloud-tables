package it.niedermann.nextcloud.tables.remote.model.columns;

import androidx.annotation.NonNull;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;

public class DateTimeColumn extends AbstractColumn {

    private final String datetimeDefault;

    public DateTimeColumn(long tableRemoteId, @NonNull Column column) {
        super(tableRemoteId, column);
        this.datetimeDefault = column.getDatetimeDefault();
    }

    public String getDatetimeDefault() {
        return datetimeDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DateTimeColumn that = (DateTimeColumn) o;
        return Objects.equals(datetimeDefault, that.datetimeDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), datetimeDefault);
    }
}
