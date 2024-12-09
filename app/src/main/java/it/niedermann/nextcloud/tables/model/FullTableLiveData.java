package it.niedermann.nextcloud.tables.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;

public class FullTableLiveData extends MediatorLiveData<FullTable> {

    private final Table table;
    private final Emitter<FullRow> rowEmitter = new Emitter<>();
    private final Emitter<FullColumn> columnEmitter = new Emitter<>();

    public FullTableLiveData(@NonNull Table table,
                             @NonNull LiveData<List<FullRow>> rowSource,
                             @NonNull LiveData<List<FullColumn>> columnRows) {
        this.table = table;
        addSource(rowSource, rowEmitter::emit);
        addSource(columnRows, columnEmitter::emit);
    }

    private class Emitter<T> {
        private boolean firstEmit = true;
        private final List<T> value = new ArrayList<>();

        // TODO find better way then synchronized
        private synchronized void emit(List<T> newValues) {
            this.firstEmit = false;
            this.value.clear();
            this.value.addAll(newValues);

            if (!rowEmitter.firstEmit && !columnEmitter.firstEmit) {
                final var dataGrid = new ArrayList<List<FullData>>(Collections.nCopies(rowEmitter.value.size(), null));

                for (int rowPosition = 0; rowPosition < rowEmitter.value.size(); rowPosition++) {
                    final var columnsForCurrentRow = new ArrayList<FullData>(Collections.nCopies(columnEmitter.value.size(), null));
                    dataGrid.set(rowPosition, columnsForCurrentRow);

                    for (int columnPosition = 0; columnPosition < columnEmitter.value.size(); columnPosition++) {
                        final var targetColumnId = columnEmitter.value.get(columnPosition).getColumn().getId();

                        final var finalColumnPosition = columnPosition;

                        rowEmitter.value.get(rowPosition).getFullData()
                                .stream()
                                .filter(data -> data.getData().getColumnId() == targetColumnId)
                                .findAny()
                                .ifPresentOrElse(
                                        data -> columnsForCurrentRow.set(finalColumnPosition, data),
                                        () -> columnsForCurrentRow.set(finalColumnPosition, new FullData())
                                );

                    }
                    dataGrid.set(rowPosition, columnsForCurrentRow);
                }

                postValue(new FullTable(table, rowEmitter.value, columnEmitter.value, dataGrid));
            }
        }
    }
}
