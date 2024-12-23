package it.niedermann.nextcloud.tables.features.row;

import static java.util.Collections.emptyMap;
import static java.util.concurrent.CompletableFuture.completedFuture;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.features.row.edit.type.DataEditView;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

public class EditRowViewModel extends AndroidViewModel {

    private final TablesRepository tablesRepository;

    public EditRowViewModel(@NonNull Application application) {
        super(application);
        tablesRepository = new TablesRepository(application);
    }

    public CompletableFuture<List<FullColumn>> getNotDeletedColumns(@NonNull Table table) {
        return tablesRepository.getNotDeletedColumns(table);
    }

    public CompletableFuture<Void> createRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Collection<DataEditView<?>> editors) {
        final var data = editors.stream().map(DataEditView::getFullData).collect(Collectors.toUnmodifiableList());
        final var row = new Row();
        row.setCreatedBy(account.getUserName());
        row.setCreatedAt(Instant.now());
        row.setLastEditBy(account.getUserName());
        row.setLastEditAt(row.getCreatedAt());
        row.setTableId(table.getId());
        return tablesRepository.createRow(account, table, row, data);
    }

    public CompletableFuture<Void> updateRow(@NonNull Account account,
                                             @NonNull Table table,
                                             @NonNull Row row,
                                             @NonNull Collection<DataEditView<?>> editors) {
        final var data = editors.stream().map(DataEditView::getFullData).collect(Collectors.toUnmodifiableList());
        return tablesRepository.updateRow(account, table, row, data);
    }

    public CompletableFuture<Map<Long, FullData>> getFullData(@Nullable Row row) {
        return row == null
                ? completedFuture(emptyMap())
                : tablesRepository.getRawColumnIdAndFullData(row.getId());
    }
}
