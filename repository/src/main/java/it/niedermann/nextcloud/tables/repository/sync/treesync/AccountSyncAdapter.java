package it.niedermann.nextcloud.tables.repository.sync.treesync;

import static java.util.concurrent.CompletableFuture.allOf;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;

class AccountSyncAdapter extends AbstractSyncAdapter<Account> {

    private static final String TAG = AccountSyncAdapter.class.getSimpleName();

    private final SyncAdapter<Account> searchProviderSyncAdapter;
    private final SyncAdapter<Account> capabilitiesSyncAdapter;
    private final SyncAdapter<Account> userSyncAdapter;
    private final SyncAdapter<Account> tableSyncAdapter;

    public AccountSyncAdapter(@NonNull Context context,
                              @Nullable SyncStatusReporter reporter) {
        this(context, reporter,
                new CapabilitiesSyncAdapter(context, reporter),
                new UserSyncAdapter(context, reporter),
                new TableSyncAdapter(context, reporter),
                new SearchProviderSyncAdapter(context, reporter));
    }

    private AccountSyncAdapter(@NonNull Context context,
                               @Nullable SyncStatusReporter reporter,
                               @NonNull SyncAdapter<Account> capabilitiesSyncAdapter,
                               @NonNull SyncAdapter<Account> userSyncAdapter,
                               @NonNull SyncAdapter<Account> tableSyncAdapters,
                               @NonNull SyncAdapter<Account> searchProviderSyncAdapter
    ) {
        super(context, reporter);
        this.capabilitiesSyncAdapter = capabilitiesSyncAdapter;
        this.userSyncAdapter = userSyncAdapter;
        this.tableSyncAdapter = tableSyncAdapters;
        this.searchProviderSyncAdapter = searchProviderSyncAdapter;
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalCreations(@NonNull Account account, @NonNull Account parentEntity) {
        return allOf(
                capabilitiesSyncAdapter.pushLocalCreations(account, parentEntity),
                userSyncAdapter.pushLocalCreations(account, parentEntity),
                tableSyncAdapter.pushLocalCreations(account, parentEntity),
                searchProviderSyncAdapter.pushLocalCreations(account, parentEntity));
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalUpdates(@NonNull Account account, @NonNull Account parentEntity) {
        return allOf(
                capabilitiesSyncAdapter.pushLocalUpdates(account, parentEntity),
                userSyncAdapter.pushLocalUpdates(account, parentEntity),
                tableSyncAdapter.pushLocalUpdates(account, parentEntity),
                searchProviderSyncAdapter.pushLocalUpdates(account, parentEntity));
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushLocalDeletions(@NonNull Account account, @NonNull Account parentEntity) {
        return allOf(
                capabilitiesSyncAdapter.pushLocalDeletions(account, parentEntity),
                userSyncAdapter.pushLocalDeletions(account, parentEntity),
                tableSyncAdapter.pushLocalDeletions(account, parentEntity),
                searchProviderSyncAdapter.pushLocalDeletions(account, parentEntity));
    }

    @NonNull
    @Override
    public CompletableFuture<Void> pushChildChangesWithoutChangedParent(@NonNull Account account) {
        return allOf(
                capabilitiesSyncAdapter.pushChildChangesWithoutChangedParent(account),
                userSyncAdapter.pushChildChangesWithoutChangedParent(account),
                tableSyncAdapter.pushChildChangesWithoutChangedParent(account),
                searchProviderSyncAdapter.pushChildChangesWithoutChangedParent(account));
    }

    /// @implNote **Guaranteed executing orders**
    ///
    /// It is guaranteed, that [SyncAdapter#pullRemoteChanges] will be called and waited for before continuing with anything else.
    ///
    /// This is to ensure that
    /// - the [Account#getTablesVersion] is ready for validations, transformations, ...
    /// - the maintenance mode can be detected properly
    @NonNull
    @Override
    public CompletableFuture<Void> pullRemoteChanges(@NonNull Account account,
                                                     @NonNull Account parentEntity) {
        return capabilitiesSyncAdapter.pullRemoteChanges(account, parentEntity)
                .thenComposeAsync(v -> allOf(
                        userSyncAdapter.pullRemoteChanges(account, parentEntity),
                        searchProviderSyncAdapter.pullRemoteChanges(account, parentEntity)
                                .thenComposeAsync(v2 -> tableSyncAdapter.pullRemoteChanges(account, parentEntity))));
    }
}
