package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.util.Log;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;

import it.niedermann.nextcloud.tables.database.DBStatus;
import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.AbstractRemoteEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;

public class ColumnSyncAdapter extends AbstractSyncAdapter {

    private static final String TAG = ColumnSyncAdapter.class.getSimpleName();

    public ColumnSyncAdapter(@NonNull TablesDatabase db) {
        super(db);
    }

    @Override
    public void pushLocalChanges(@NonNull TablesAPI api, @NonNull Account account) throws IOException, NextcloudHttpRequestFailedException {
        Log.v(TAG, "Pushing local columns for " + account.getAccountName());
        final var columnsToDelete = db.getColumnDao().getColumns(account.getId(), DBStatus.LOCAL_DELETED);
        for (final var column : columnsToDelete) {
            Log.i(TAG, "→ DELETE: " + column.getTitle());
            final var response = api.deleteColumn(column.getRemoteId()).execute();
            Log.i(TAG, "-→ HTTP " + response.code());
            if (response.isSuccessful()) {
                db.getColumnDao().delete(column);
            } else {
                throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException("Could not delete column " + column.getTitle()));
            }
        }

        final var columnsToUpdate = db.getColumnDao().getColumns(account.getId(), DBStatus.LOCAL_EDITED);
        for (final var column : columnsToUpdate) {
            Log.i(TAG, "→ PUT: " + column.getTitle());
            final var response = api.updateColumn(column.getRemoteId(), column).execute();
            Log.i(TAG, "-→ HTTP " + response.code());
            if (response.isSuccessful()) {
                column.setStatus(DBStatus.VOID);
                db.getColumnDao().update(column);
            } else {
                throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException("Could not push local changes for table " + column.getTitle()));
            }
        }
    }

    @Override
    public void pullRemoteChanges(@NonNull TablesAPI api, @NonNull Account account) throws IOException, NextcloudHttpRequestFailedException {
        for (final var table : db.getTableDao().getTables(account.getId())) {
            final var request = api.getColumns(table.getRemoteId());
            final var response = request.execute();
            switch (response.code()) {
                case 200: {
                    final var columns = response.body();
                    if (columns == null) {
                        throw new RuntimeException("Response body is null");
                    }

                    final var columnRemoteIds = columns.stream().map(AbstractRemoteEntity::getRemoteId).collect(toUnmodifiableSet());
                    final var columnIds = db.getColumnDao().getColumnRemoteAndLocalIds(account.getId(), columnRemoteIds);

                    for (final var column : columns) {
                        column.setAccountId(account.getId());
                        column.setTableId(table.getId());
                        column.setETag(response.headers().get(HEADER_ETAG));

                        final var columnId = columnIds.get(column.getRemoteId());
                        if (columnId == null) {
                            Log.i(TAG, "→ Adding column " + column.getTitle() + " to database");
                            db.getColumnDao().insert(column);
                        } else {
                            column.setId(columnId);
                            Log.i(TAG, "→ Updating column " + column.getTitle() + " in database");
                            db.getColumnDao().update(column);
                        }
                    }

                    Log.i(TAG, "→ Delete all columns except remoteId " + columnRemoteIds);
                    db.getColumnDao().deleteExcept(table.getId(), columnRemoteIds);
                    break;
                }

                case 304: {
                    Log.v(TAG, "Pull remote columns: HTTP " + response.code() + " Not Modified");
                    break;
                }

                default: {
                    throw new NextcloudHttpRequestFailedException(response.code(), new RuntimeException());
                }
            }
        }
    }
}
