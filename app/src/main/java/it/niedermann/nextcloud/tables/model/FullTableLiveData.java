package it.niedermann.nextcloud.tables.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;

public class FullTableLiveData extends MediatorLiveData<FullTable> {

    private final Emitter<Row> rowEmitter = new Emitter<>();
    private final Emitter<Column> columnEmitter = new Emitter<>();
    private final Emitter<Data> dataEmitter = new Emitter<>();

    public FullTableLiveData(@NonNull LiveData<List<Row>> rowSource,
                             @NonNull LiveData<List<Column>> columnRows,
                             @NonNull LiveData<List<Data>> dataSource) {
        addSource(rowSource, rowEmitter::emit);
        addSource(columnRows, columnEmitter::emit);
        addSource(dataSource, dataEmitter::emit);
    }

    private class Emitter<T> {
        private boolean firstEmit = true;
        private final List<T> value = new ArrayList<>();

        // TODO find better way then synchronized
        private synchronized void emit(List<T> newValues) {
            this.firstEmit = false;
            this.value.clear();
            this.value.addAll(newValues);

            if (!rowEmitter.firstEmit && !columnEmitter.firstEmit && !dataEmitter.firstEmit) {
                final var rows = new ArrayList<List<Data>>(Collections.nCopies(rowEmitter.value.size(), null));

                for (int rowPosition = 0; rowPosition < rowEmitter.value.size(); rowPosition++) {
                    final var columnsForCurrentRow = new ArrayList<Data>(Collections.nCopies(columnEmitter.value.size(), null));
                    rows.set(rowPosition, columnsForCurrentRow);

                    for (int columnPosition = 0; columnPosition < columnEmitter.value.size(); columnPosition++) {
                        final var targetRowId = rowEmitter.value.get(rowPosition).getId();
                        final var targetColumnId = columnEmitter.value.get(columnPosition).getId();

                        final var finalColumnPosition = columnPosition;

                        dataEmitter.value
                                .stream()
                                .filter(data -> data.getRowId() == targetRowId)
                                .filter(data -> data.getColumnId() == targetColumnId)
                                .findAny()
                                .ifPresentOrElse(
                                        data -> columnsForCurrentRow.set(finalColumnPosition, data),
                                        () -> columnsForCurrentRow.set(finalColumnPosition, null)
                                );

                    }
                    rows.set(rowPosition, columnsForCurrentRow);
                }

                postValue(new FullTable(rowEmitter.value, columnEmitter.value, rows));
            }
        }
    }
}
