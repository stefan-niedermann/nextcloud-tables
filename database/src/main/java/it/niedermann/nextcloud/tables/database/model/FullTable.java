package it.niedermann.nextcloud.tables.database.model;

import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;

public class FullTable {

    @Nullable
    @Embedded
    private Table table;

    @Nullable
    @Relation(
            parentColumn = "accountId",
            entityColumn = "id"
    )
    private Account account;

    @NonNull
    @Relation(
            parentColumn = "id",
            entityColumn = "tableId",
            entity = Column.class
    )
    private List<FullColumn> columns = Collections.emptyList();

    @NonNull
    @Relation(
            entity = Row.class,
            parentColumn = "id",
            entityColumn = "tableId"
    )
    private List<FullRow> rows = Collections.emptyList();

    public FullTable() {
        // Default constructor
    }

    @NonNull
    public Table getTable() {
        return requireNonNull(table);
    }

    public void setTable(@NonNull Table table) {
        this.table = table;
    }

    @NonNull
    public Account getAccount() {
        return requireNonNull(account);
    }

    public void setAccount(@NonNull Account account) {
        this.account = account;
    }

    @NonNull
    public List<FullColumn> getColumns() {
        return columns;
    }

    public void setColumns(@NonNull List<FullColumn> columns) {
        this.columns = columns;
    }

    @NonNull
    public List<FullRow> getRows() {
        return rows;
    }

    public void setRows(@NonNull List<FullRow> rows) {
        this.rows = rows;
    }

    @NonNull
    @Override
    public String toString() {
        return requireNonNull(table).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullTable fullTable = (FullTable) o;
        return Objects.equals(table, fullTable.table) && Objects.equals(account, fullTable.account) && Objects.equals(columns, fullTable.columns) && Objects.equals(rows, fullTable.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, account, columns, rows);
    }
}
