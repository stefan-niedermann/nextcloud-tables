package it.niedermann.nextcloud.tables.repository.sync;

import android.content.Context;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.tablesV1.TablesV1API;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;
import it.niedermann.nextcloud.tables.repository.ServerErrorHandler;

public abstract class AbstractSyncAdapter {

    protected static final String HEADER_ETAG = "ETag";
    protected final TablesDatabase db;
    protected final ServerErrorHandler serverErrorHandler;

    protected AbstractSyncAdapter(@NonNull TablesDatabase db, @NonNull Context context) {
        this.db = db;
        this.serverErrorHandler = new ServerErrorHandler(context);
    }

    public abstract void pushLocalChanges(@NonNull TablesV2API api,
                                          @NonNull TablesV1API apiV1,
                                          @NonNull Account account) throws Exception;

    public abstract void pullRemoteChanges(@NonNull TablesV2API api,
                                           @NonNull TablesV1API apiV1,
                                           @NonNull Account account) throws Exception;
}
