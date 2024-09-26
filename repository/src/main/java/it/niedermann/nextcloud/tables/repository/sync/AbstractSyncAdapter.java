package it.niedermann.nextcloud.tables.repository.sync;

import android.content.Context;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;
import it.niedermann.nextcloud.tables.remote.api.TablesV1API;
import it.niedermann.nextcloud.tables.repository.ServerErrorHandler;

public abstract class AbstractSyncAdapter {

    protected static final String HEADER_ETAG = "ETag";
    protected final TablesDatabase db;
    protected final ServerErrorHandler serverErrorHandler;

    protected AbstractSyncAdapter(@NonNull TablesDatabase db, @NonNull Context context) {
        this.db = db;
        this.serverErrorHandler = new ServerErrorHandler(context);
    }

    public abstract void pushLocalChanges(@NonNull TablesAPI api,
                                          @NonNull TablesV1API apiV1,
                                          @NonNull Account account) throws Exception;

    public abstract void pullRemoteChanges(@NonNull TablesAPI api,
                                           @NonNull TablesV1API apiV1,
                                           @NonNull Account account) throws Exception;
}
