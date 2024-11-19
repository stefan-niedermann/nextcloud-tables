package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Value {

    @Nullable
    private String stringValue;

    @Nullable
    private Boolean booleanValue;

    @Nullable
    private Double doubleValue;

    @Nullable
    private Instant instantValue;

    @Nullable
    private LocalDate dateValue;

    @Nullable
    private LocalTime timeValue;

    public Value() {
        // Default constructor
    }

    @Nullable
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(@Nullable String stringValue) {
        this.stringValue = stringValue;
    }

    @Nullable
    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(@Nullable Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    @Nullable
    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(@Nullable Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Nullable
    public Instant getInstantValue() {
        return instantValue;
    }

    public void setInstantValue(@Nullable Instant instantValue) {
        this.instantValue = instantValue;
    }

    @Nullable
    public LocalDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(@Nullable LocalDate dateValue) {
        this.dateValue = dateValue;
    }

    @Nullable
    public LocalTime getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(@Nullable LocalTime timeValue) {
        this.timeValue = timeValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return Objects.equals(stringValue, value.stringValue) && Objects.equals(booleanValue, value.booleanValue) && Objects.equals(doubleValue, value.doubleValue) && Objects.equals(instantValue, value.instantValue) && Objects.equals(dateValue, value.dateValue) && Objects.equals(timeValue, value.timeValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringValue, booleanValue, doubleValue, instantValue, dateValue, timeValue);
    }

    @NonNull
    @Override
    public String toString() {
        return Stream.of(stringValue, booleanValue, doubleValue, instantValue, dateValue, timeValue)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}