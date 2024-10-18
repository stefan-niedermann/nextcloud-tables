package it.niedermann.nextcloud.tables.repository.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Account;

public class ImportState {
    @NonNull
    public final State state;
    @Nullable
    public final Account account;
    @Nullable
    public final Throwable error;
    @Nullable
    public final Float progress;

    public ImportState(@NonNull State state, @NonNull Account account) {
        this(state, account, null, null);
    }

    public ImportState(@NonNull Account account) {
        this(State.IMPORTING_ACCOUNT, account, null, null);
    }

    public ImportState(@NonNull Account account, float progress) {
        this(State.IMPORTING_TABLES, account, progress, null);
    }

    public ImportState(@Nullable Account account, @NonNull Throwable error) {
        this(State.ERROR, account, null, error);
    }

    private ImportState(@NonNull State state,
                        @Nullable Account account,
                        @Nullable Float progress,
                        @Nullable Throwable error) {
        this.state = state;
        this.account = account;
        this.progress = progress;
        this.error = error;
    }

    public enum State {
        IMPORTING_ACCOUNT,
        IMPORTING_TABLES,
        FINISHED,
        ERROR,
    }
}