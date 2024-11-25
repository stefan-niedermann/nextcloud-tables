package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.completedFuture;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import it.niedermann.nextcloud.tables.database.entity.Account;

class UserSyncAdapter extends AbstractSyncAdapter {


    public UserSyncAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public @NonNull CompletableFuture<Account> pushLocalChanges(@NonNull Account account) {
        // Users can't be changed locally
        return completedFuture(account);
    }

    @Override
    public @NonNull CompletableFuture<Account> pullRemoteChanges(@NonNull Account account) {
        return executeNetworkRequest(account, apis -> apis.ocs().getUser(account.getUserName()))
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
                .thenAcceptAsync(db.getAccountDao()::update, db.getSequentialExecutor())
                .thenApplyAsync(v -> account, workExecutor);
    }
}
