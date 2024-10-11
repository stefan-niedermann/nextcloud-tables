package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;
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

    @NonNull
    @Embedded
    public Table table;

    @NonNull
    @Relation(
            parentColumn = "accountId",
            entityColumn = "id"
    )
    public Account account;

    @NonNull
    @Relation(
            parentColumn = "id",
            entityColumn = "tableId"
    )
    public List<Column> columns;

    @NonNull
    @Relation(
            entity = Row.class,
            parentColumn = "id",
            entityColumn = "tableId"
    )
    public List<FullRow> rows;

    public FullTable() {
        this.table = new Table();
        this.account = new Account();
        this.columns = Collections.emptyList();
        this.rows = Collections.emptyList();
    }

    @NonNull
    @Override
    public String toString() {
        return table.toString();
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
