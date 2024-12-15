package it.niedermann.nextcloud.tables.ui.importaccount;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.exception.AccountAlreadyImportedException;
import it.niedermann.nextcloud.tables.repository.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatus;

public record ImportAccountUIState(
        @Nullable Account account,
        boolean importRunning,
        @Nullable Throwable error,
        @Nullable String progressText,
        @Nullable Integer progressTotal,
        @Nullable Integer progress,
        @Nullable Integer progressSecondary
) {

    public ImportAccountUIState() {
        this(false);
    }

    public ImportAccountUIState(final boolean importRunning) {
        this(null, importRunning, null, null, null, null, null);
    }

    public ImportAccountUIState(@Nullable Throwable throwable) {
        this(null, false, throwable, null, null, null, null);
    }

    public ImportAccountUIState(@NonNull final Context context, @NonNull final SyncStatus syncStatus) {
        this(syncStatus.getAccount(),
                !syncStatus.isFinished(),
                syncStatus.getError(),
                switch (syncStatus.getStep()) {
                    case START -> context.getString(R.string.import_state_import_account);
                    case PROGRESS -> context.getString(R.string.import_state_import_tables);
                    case FINISHED -> null;
                    case ERROR -> {
                        if (syncStatus.getError() instanceof AccountAlreadyImportedException) {
                            yield context.getString(R.string.account_already_imported);
                        } else {
                            if (syncStatus.getError() != null) {
                                if (syncStatus.getError() instanceof ServerNotAvailableException) {
                                    yield context.getString(((ServerNotAvailableException) syncStatus.getError()).getReason().messageRes);
                                } else {
                                    yield syncStatus.getError().getMessage();
                                }
                            } else {
                                yield context.getString(R.string.hint_error_appeared);
                            }
                        }
                    }
                },
                syncStatus.getTablesTotalCount().orElse(100),
                syncStatus.getTablesFinishedCount().orElse(0),
                syncStatus.getTablesFinishedCount().orElse(0) + syncStatus.getTablesInProgress().size());
    }

    @IntRange(from = View.VISIBLE, to = View.GONE)
    public int getProgressTextVisibility() {
        return TextUtils.isEmpty(progressText) ? View.GONE : View.VISIBLE;
    }

    @IntRange(from = View.VISIBLE, to = View.GONE)
    public int getProgressBarVisibility() {
        return importRunning ? View.VISIBLE : View.GONE;
    }

    public int getProgressTotal() {
        return 100;
    }

    public boolean isProgressIndeterminate() {
        return importRunning && progress == null && progressSecondary != null && progressSecondary > 0;
    }
}