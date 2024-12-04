package it.niedermann.nextcloud.tables.model;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;

public record FullTable(
        Table table,
        List<FullRow> fullRows,
        List<FullColumn> fullColumns,
        List<List<FullData>> data) {
}
