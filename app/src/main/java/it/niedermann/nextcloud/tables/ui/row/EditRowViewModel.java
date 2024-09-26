package it.niedermann.nextcloud.tables.ui.row;

import static java.util.Collections.emptyMap;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toUnmodifiableMap;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.repository.TablesRepository;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;

public class EditRowViewModel extends AndroidViewModel {

    private final TablesRepository tablesRepository;
    private final ExecutorService executor;

    public EditRowViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
        executor = Executors.newSingleThreadExecutor();
    }

    public CompletableFuture<List<Column>> getNotDeletedColumns(@NonNull Table table) {
        return supplyAsync(() -> tablesRepository.getNotDeletedColumns(table), executor);
    }

    public CompletableFuture<Void> createRow(@NonNull Account account, @NonNull Table table, @NonNull Collection<ColumnEditView> editors) {
        return supplyAsync(() -> {
            final var data = editors.stream().map(ColumnEditView::toData).toArray(Data[]::new);
            final var row = new Row();
            row.setCreatedBy(account.getUserName());
            row.setCreatedAt(Instant.now());
            row.setLastEditBy(account.getUserName());
            row.setLastEditAt(row.getCreatedAt());
            row.setTableId(table.getId());
            try {
                tablesRepository.createRow(account, table, row, data);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Void> updateRow(@NonNull Account account, @NonNull Table table, @NonNull Row row, @NonNull Collection<ColumnEditView> editors) {
        return supplyAsync(() -> {
            final var data = editors.stream().map(ColumnEditView::toData).toArray(Data[]::new);
            try {
                tablesRepository.updateRow(account, table, row, data);
                return null;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    public CompletableFuture<Map<Long, Data>> getData(@Nullable Row row) {
        if (row == null) {
            return completedFuture(emptyMap());
        }

        return supplyAsync(() -> {
            // TODO perf: maybe we can query a map directly from the database
            final var dataset = tablesRepository.getRawData(row.getId());

            if (dataset == null) {
                return emptyMap();
            }

            return Arrays.stream(dataset).collect(toUnmodifiableMap(Data::getColumnId, data -> data));
        }, executor);
    }
}
