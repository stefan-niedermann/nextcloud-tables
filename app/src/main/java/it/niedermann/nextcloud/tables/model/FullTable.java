package it.niedermann.nextcloud.tables.model;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;

public class FullTable {

    private final Table table;
    private final List<Row> rows;
    private final List<Column> columns;
    private final List<List<Data>> data;

    public FullTable(@NonNull Table table, @NonNull List<Row> rows, @NonNull List<Column> columns, @NonNull List<List<Data>> data) {
        this.table = table;
        this.rows = rows;
        this.columns = columns;
        this.data = data;
    }

    public Table getTable() {
        return table;
    }

    public List<Row> getRows() {
        return rows;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<List<Data>> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullTable fullTable = (FullTable) o;
        return Objects.equals(table, fullTable.table) && Objects.equals(rows, fullTable.rows) && Objects.equals(columns, fullTable.columns) && Objects.equals(data, fullTable.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, rows, columns, data);
    }
}
