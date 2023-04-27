package it.niedermann.nextcloud.tables.repository.sync;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.IOException;

import it.niedermann.nextcloud.tables.database.TablesDatabase;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.remote.api.TablesAPI;

public abstract class AbstractSyncAdapter {

    protected static final String HEADER_ETAG = "ETag";
    protected final TablesDatabase db;

    protected AbstractSyncAdapter(@NonNull TablesDatabase db) {
        this.db = db;
    }

    public abstract void pushLocalChanges(@NonNull TablesAPI api, @NonNull Account account) throws IOException, NextcloudHttpRequestFailedException;

    public abstract void pullRemoteChanges(@NonNull TablesAPI api, @NonNull Account account) throws IOException, NextcloudHttpRequestFailedException;
}
