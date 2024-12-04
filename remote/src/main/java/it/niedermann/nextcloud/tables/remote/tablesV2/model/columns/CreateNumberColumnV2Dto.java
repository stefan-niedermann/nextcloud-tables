package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;

import java.util.Objects;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;

public class CreateNumberColumnV2Dto extends CreateColumnV2Dto {

    private final Double numberDefault;
    private final Integer numberDecimals;
    private final String numberPrefix;
    private final String numberSuffix;
    private final Double numberMin;
    private final Double numberMax;

    public CreateNumberColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto column) {
        super(tableRemoteId, column);
        this.numberDefault = column.numberDefault();
        this.numberDecimals = column.numberDecimals();
        this.numberPrefix = column.numberPrefix();
        this.numberSuffix = column.numberSuffix();
        this.numberMin = column.numberMin();
        this.numberMax = column.numberMax();
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
        CreateNumberColumnV2Dto that = (CreateNumberColumnV2Dto) o;
        return Objects.equals(numberDefault, that.numberDefault) && Objects.equals(numberDecimals, that.numberDecimals) && Objects.equals(numberPrefix, that.numberPrefix) && Objects.equals(numberSuffix, that.numberSuffix) && Objects.equals(numberMin, that.numberMin) && Objects.equals(numberMax, that.numberMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numberDefault, numberDecimals, numberPrefix, numberSuffix, numberMin, numberMax);
    }
}
