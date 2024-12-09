package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.completedFuture;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;

abstract class AbstractPullOnlySyncAdapter extends AbstractSyncAdapter<Account> {

    protected AbstractPullOnlySyncAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalCreations(@NonNull Account account, @NonNull Account entity) {
        return completedFuture(null);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalUpdates(@NonNull Account account, @NonNull Account entity) {
        return completedFuture(null);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalDeletions(@NonNull Account account, @NonNull Account entity) {
        return completedFuture(null);
    }
}
