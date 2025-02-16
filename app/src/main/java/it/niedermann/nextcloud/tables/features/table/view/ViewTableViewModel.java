package it.niedermann.nextcloud.tables.features.table.view;

import android.app.Application;
import android.util.Range;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullTable;
import it.niedermann.nextcloud.tables.repository.AccountRepository;
import it.niedermann.nextcloud.tables.repository.TablesRepository;

@MainThread
public class ViewTableViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final AccountRepository accountRepository;
    private final TablesRepository tablesRepository;

    private final LiveData<Boolean> userInitiatedTableChangeActive;
    private final MutableLiveData<TableFilter> tableFilter;

    public ViewTableViewModel(@NonNull Application application,
                              @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        accountRepository = new AccountRepository(application);
        tablesRepository = new TablesRepository(application);

        userInitiatedTableChangeActive = savedStateHandle.getLiveData("userInitiatedTableChangeActive", false);
        tableFilter = savedStateHandle.getLiveData("tableFilter", null);
    }

    @NonNull
    public LiveData<Account> getCurrentAccount$() {
        return accountRepository.getCurrentAccount();
    }

    @NonNull
    public LiveData<UiState> getUiState() {
        return new ReactiveLiveData<>(getCurrentAccount$())
                .flatMap(account -> {
                    if (account == null) {
                        return new MutableLiveData<>();
                    }

                    if (account.getCurrentTable() == null) {
                        return new MutableLiveData<>(new UiState(account, null, Collections.emptyList()));
                    }

                    return new ReactiveLiveData<>(tablesRepository.getNotDeletedTable$(account.getCurrentTable()))
                            .flatMap(this::getFullTable$)
                            .map(fullTable -> {
                                final var dataGrid = Optional
                                        .ofNullable(fullTable)
                                        .map(this::dataToGrid)
                                        .orElseGet(Collections::emptyList);
                                return new UiState(account, fullTable, dataGrid);
                            });
                })
                .distinctUntilChanged();
    }

    @NonNull
    public LiveData<FullTable> getFullTable$(@Nullable Table table) {
        if (table == null) {
            return new MutableLiveData<>(null);
        }

        return new ReactiveLiveData<>(tableFilter)
                .map(tableFilter -> Optional
                        .ofNullable(tableFilter)
                        .or(() -> Optional
                                .ofNullable(table.getCurrentRow())
                                .map(currentRow -> new TableFilter(currentRow, currentRow)))
                        .map(tf -> new Range<>(tf.requestedMinRowPosition(), tf.requestedMaxRowPosition()))
                        .map(range -> new Range<>(Math.max(0L, range.getLower() - 10), range.getUpper() + 10))
                        .orElse(new Range<>(0L, 0L)))
                .flatMap(range -> {
                    tablesRepository.updateCurrentRow(table.getId(), range.getLower());
                    return tablesRepository.getFullTable$(table.getId(), range);
                });
    }

    public void requestRowPositionRange(@NonNull Range<Long> tableFilter) {
        savedStateHandle.set("tableFilter", new TableFilter(tableFilter.getLower(), tableFilter.getUpper()));
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteRow(@NonNull Table table, @NonNull Row row) {
        return tablesRepository.deleteRow(table, row);
    }

    @AnyThread
    @NonNull
    public CompletableFuture<Void> deleteColumn(@NonNull Table table, @NonNull Column column) {
        return tablesRepository.deleteColumn(table, column);
    }

    /// @return a two dimensional grid of `FullData`, the outer `List` are the `FullRows`,
    /// the inner `List` are the `Columns`. `FullData` is copied into the grid by reference.
    private List<List<FullData>> dataToGrid(@NonNull FullTable fullTable) {
        final var fullRows = fullTable.getRows();
        final var fullColumns = fullTable.getColumns();

        // TODO Idea to speed up: Query Map<Long, Column> / Map<Long, Row>

        // TODO Migrate FullData to a database View
        // We must sort our data here because Rooms @Relation does not allow ordering within FullTable
        // However, we only have to sort the columns and the rows, not the data itself because accessing the data happens via an index based access to the dataGrid
        Collections.sort(fullColumns);
        Collections.sort(fullRows);

        if (fullRows.isEmpty() || fullColumns.isEmpty()) {
            return Collections.emptyList();
        }

        final var dataGrid = new ArrayList<List<FullData>>(Collections.nCopies(fullRows.size(), null));

        for (int rowPosition = 0; rowPosition < fullRows.size(); rowPosition++) {
            final var columnsForCurrentRow = new ArrayList<FullData>(Collections.nCopies(fullColumns.size(), null));
            dataGrid.set(rowPosition, columnsForCurrentRow);

            for (int columnPosition = 0; columnPosition < fullColumns.size(); columnPosition++) {

                final var targetColumnId = fullColumns.get(columnPosition).getColumn().getId();
                final var finalColumnPosition = columnPosition;

                fullRows.get(rowPosition).getFullData()
                        .stream()
                        .filter(data -> data.getData().getColumnId() == targetColumnId)
                        .findAny()
                        .ifPresentOrElse(
                                data -> columnsForCurrentRow.set(finalColumnPosition, data),
                                () -> columnsForCurrentRow.set(finalColumnPosition, new FullData()));

            }
            dataGrid.set(rowPosition, columnsForCurrentRow);
        }

        return dataGrid;
    }

    public record UiState(
            @NonNull Account account,
            @Nullable FullTable currentFullTable,
            @NonNull List<List<FullData>> dataGrid
    ) {
    }

    public record TableFilter(
            long requestedMinRowPosition,
            long requestedMaxRowPosition
    ) implements Serializable {
    }
}
