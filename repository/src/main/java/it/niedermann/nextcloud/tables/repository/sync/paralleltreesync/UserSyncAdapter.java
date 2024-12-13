package it.niedermann.nextcloud.tables.repository.sync.paralleltreesync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;

class UserSyncAdapter extends AbstractPullOnlySyncAdapter {

    public UserSyncAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account,
                                                     @NonNull Account parentEntity,
                                                     @Nullable SyncStatusReporter reporter) {
        return requestHelper.executeNetworkRequest(account, apis -> apis.ocs().getUser(account.getUserName()))
                .thenApplyAsync(response -> switch (response.code()) {
                    case 200 -> {
                        final var body = response.body();
                        if (body == null) {
                            throw new RuntimeException("Response body is null");
                        }

                        account.setDisplayName(body.ocs.data.displayName);
                        yield account;
                    }
                    default -> {
                        final var exception = serverErrorHandler.responseToException(response, "Could not fetch user " + account.getUserName(), true);

                        if (exception.isPresent()) {
                            throw new CompletionException(exception.get());
                        }
                        yield account;
                    }
                })
                .thenAcceptAsync(db.getAccountDao()::update, db.getSequentialExecutor());
    }
}
