package it.niedermann.nextcloud.tables.repository.sync.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;

public class SyncStatus {

    @NonNull
    private final Account account;
    @NonNull
    private final Step step;
    @NonNull
    private final Collection<Table> tablesInProgress = new LinkedList<>();
    @Nullable
    private final Integer tablesTotalCount;
    @Nullable
    private final Integer tablesFinishedCount;
    @Nullable
    private final Throwable error;

    public SyncStatus(@NonNull Account account) {
        this(account, Step.START, Collections.emptySet(), null, null, null);
    }

    private SyncStatus(@NonNull Account account,
                       @NonNull Step step,
                       @Nullable Collection<Table> tablesInProgress,
                       @Nullable Integer tablesTotalCount,
                       @Nullable Integer tablesFinishedCount,
                       @Nullable Throwable error) {
        this.account = account;
        this.step = step;
        this.tablesInProgress.addAll(tablesInProgress);
        this.tablesTotalCount = tablesTotalCount;
        this.tablesFinishedCount = tablesFinishedCount;
        this.error = error;
    }

    @NonNull
    public SyncStatus withTableTotalCount(@Nullable Integer tablesTotalCount) {
        return new SyncStatus(account, step, tablesInProgress, tablesTotalCount, tablesFinishedCount, error);
    }

    @NonNull
    public SyncStatus withTableProgressStarting(@NonNull Table starting) {

        final var tablesInProgress = new LinkedList<>(this.tablesInProgress);

        tablesInProgress.add(starting);

        return new SyncStatus(account, Step.PROGRESS, tablesInProgress, increment(tablesTotalCount), tablesFinishedCount, error);

    }

    @NonNull
    public SyncStatus withTableProgressFinished(@NonNull Table finished) {

        if (!Objects.equals(account.getId(), finished.getAccountId())) {
            throw new IllegalArgumentException("Argument must have same accountId as initial " + Account.class.getSimpleName());
        }

        final var tablesInProgress = new LinkedList<>(this.tablesInProgress);

        tablesInProgress.removeIf(table -> Objects.equals(table.getId(), finished.getId()) || Objects.equals(table.getRemoteId(), finished.getRemoteId()));

        return new SyncStatus(account, step, tablesInProgress, tablesTotalCount, increment(tablesFinishedCount), error);

    }

    @NonNull
    public SyncStatus withError(@NonNull Throwable error) {
        return new SyncStatus(account, Step.ERROR, tablesInProgress, tablesTotalCount, tablesFinishedCount, error);
    }

    @NonNull
    public SyncStatus markAsFinished() {

        if (!tablesInProgress.isEmpty()) {
            throw new IllegalStateException(Step.FINISHED + "can not be set while tables still being in progress: " + tablesInProgress);
        }

        return new SyncStatus(account, Step.FINISHED, tablesInProgress, tablesTotalCount, tablesFinishedCount, error);

    }

    public boolean isFinished() {
        return step.isEndStep;
    }

    private int increment(@Nullable Integer source) {
        return Optional.ofNullable(source).map(value -> value + 1).orElse(1);
    }

    @NonNull
    public Step getStep() {
        return step;
    }

    @NonNull
    public Optional<Integer> getTablesTotalCount() {
        return Optional.ofNullable(tablesTotalCount);
    }

    @NonNull
    public Optional<Integer> getTablesFinishedCount() {
        return Optional.ofNullable(tablesFinishedCount);
    }

    @NonNull
    public Collection<String> getTablesInProgress() {
        return tablesInProgress
                .stream()
                .map(Table::getTitleWithEmoji)
                .collect(Collectors.toUnmodifiableList());
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    @NonNull
    public Account getAccount() {
        return account;
    }

    public enum Step {
        START(),
        PROGRESS(),
        FINISHED(true),
        ERROR(true),
        ;

        private final boolean isEndStep;

        Step() {
            this.isEndStep = false;
        }

        Step(boolean isEndStep) {
            this.isEndStep = isEndStep;
        }
    }
}
