package it.niedermann.nextcloud.tables.remote.model.columns;

import androidx.annotation.NonNull;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;

public class NumberColumn extends AbstractColumn {

    private final Double numberDefault;
    private final Integer numberDecimals;
    private final String numberPrefix;
    private final String numberSuffix;
    private final Double numberMin;
    private final Double numberMax;

    public NumberColumn(long tableRemoteId, @NonNull Column column) {
        super(tableRemoteId, column);
        this.numberDefault = column.getNumberDefault();
        this.numberDecimals = column.getNumberDecimals();
        this.numberPrefix = column.getNumberPrefix();
        this.numberSuffix = column.getNumberSuffix();
        this.numberMin = column.getNumberMin();
        this.numberMax = column.getNumberMax();
    }

    public Double getNumberDefault() {
        return numberDefault;
    }

    public Integer getNumberDecimals() {
        return numberDecimals;
    }

    public String getNumberPrefix() {
        return numberPrefix;
    }

    public String getNumberSuffix() {
        return numberSuffix;
    }

    public Double getNumberMin() {
        return numberMin;
    }

    public Double getNumberMax() {
        return numberMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NumberColumn that = (NumberColumn) o;
        return Objects.equals(numberDefault, that.numberDefault) && Objects.equals(numberDecimals, that.numberDecimals) && Objects.equals(numberPrefix, that.numberPrefix) && Objects.equals(numberSuffix, that.numberSuffix) && Objects.equals(numberMin, that.numberMin) && Objects.equals(numberMax, that.numberMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numberDefault, numberDecimals, numberPrefix, numberSuffix, numberMin, numberMax);
    }
}
