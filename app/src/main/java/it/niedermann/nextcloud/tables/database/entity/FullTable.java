package it.niedermann.nextcloud.tables.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

/**
 * Containing the complete data of a {@link Table} including {@link Column} and {@link Row} information with {@link Data}
 */
public class FullTable implements Serializable {

    @Embedded
    private Table table;
    @Relation(parentColumn = "id", entityColumn = "tableId")
    private List<Column> columns;
    @Relation(parentColumn = "id", entityColumn = "tableId")
    private List<Row> rows;
//    @Relation(parentColumn = "id", entityColumn = "tableId")
//    private List<Data> data;
}
