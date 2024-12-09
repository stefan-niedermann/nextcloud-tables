package it.niedermann.nextcloud.tables.repository.sync;

import static java.util.concurrent.CompletableFuture.allOf;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;

class AccountSyncAdapter extends AbstractSyncAdapter<Account> {

    private static final String TAG = AccountSyncAdapter.class.getSimpleName();

    private final SyncAdapter<Account> capabilitiesSyncAdapter;
    private final SyncAdapter<Account> userSyncAdapter;
    private final SyncAdapter<Account> tableSyncAdapter;

    public AccountSyncAdapter(@NonNull Context context) {
        this(context,
                new CapabilitiesSyncAdapter(context),
                new UserSyncAdapter(context),
                new TableSyncAdapter(context));
    }

    private AccountSyncAdapter(@NonNull Context context,
                               @NonNull SyncAdapter<Account> capabilitiesSyncAdapter,
                               @NonNull SyncAdapter<Account> userSyncAdapter,
                               @NonNull SyncAdapter<Account> tableSyncAdapters
    ) {
        super(context);
        this.capabilitiesSyncAdapter = capabilitiesSyncAdapter;
        this.userSyncAdapter = userSyncAdapter;
        this.tableSyncAdapter = tableSyncAdapters;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalCreations(@NonNull Account account, @NonNull Account parentEntity) {
        return allOf(
                capabilitiesSyncAdapter.pushLocalCreations(account, parentEntity),
                userSyncAdapter.pushLocalCreations(account, parentEntity),
                tableSyncAdapter.pushLocalCreations(account, parentEntity));
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalUpdates(@NonNull Account account, @NonNull Account parentEntity) {
        return allOf(
                capabilitiesSyncAdapter.pushLocalUpdates(account, parentEntity),
                userSyncAdapter.pushLocalUpdates(account, parentEntity),
                tableSyncAdapter.pushLocalUpdates(account, parentEntity));
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalDeletions(@NonNull Account account, @NonNull Account parentEntity) {
        return allOf(
                capabilitiesSyncAdapter.pushLocalDeletions(account, parentEntity),
                userSyncAdapter.pushLocalDeletions(account, parentEntity),
                tableSyncAdapter.pushLocalDeletions(account, parentEntity));
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushChildChangesWithoutChangedParent(@NonNull Account account) {
        return allOf(
                capabilitiesSyncAdapter.pushChildChangesWithoutChangedParent(account),
                userSyncAdapter.pushChildChangesWithoutChangedParent(account),
                tableSyncAdapter.pushChildChangesWithoutChangedParent(account));
    }

    /// It is guaranteed, that [CapabilitiesSyncAdapter#pullRemoteChanges] will be called and waited for before continuing with anything else.
    /// This is to ensure that
    /// - the [Account#getTablesVersion] is ready for validations, transformations, ...
    /// - the maintenance mode can be detected properly
    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account,
                                                     @NonNull Account parentEntity,
                                                     @Nullable SyncStatusReporter reporter) {
        return capabilitiesSyncAdapter.pullRemoteChanges(account, parentEntity, reporter)
                .thenComposeAsync(v -> allOf(
                        userSyncAdapter.pullRemoteChanges(account, parentEntity, reporter),
                        tableSyncAdapter.pullRemoteChanges(account, parentEntity, reporter)));
    }
}
