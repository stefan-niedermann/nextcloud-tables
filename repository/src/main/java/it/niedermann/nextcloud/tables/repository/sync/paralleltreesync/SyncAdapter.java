package it.niedermann.nextcloud.tables.repository.sync.paralleltreesync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.tables.database.entity.AbstractEntity;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatusReporter;

interface SyncAdapter<TParentEntity extends AbstractEntity> {

    @NonNull
    CompletableFuture<Void> synchronize(@NonNull Account account, @NonNull TParentEntity parentEntity, @Nullable SyncStatusReporter reporter);

    @NonNull
    CompletableFuture<Void> pushLocalCreations(@NonNull Account account, @NonNull TParentEntity parentEntity);

    @NonNull
    CompletableFuture<Void> pushLocalUpdates(@NonNull Account account, @NonNull TParentEntity parentEntity);

    @NonNull
    CompletableFuture<Void> pushLocalDeletions(@NonNull Account account, @NonNull TParentEntity parentEntity);

    @NonNull
    CompletableFuture<Void> pushChildChangesWithoutChangedParent(@NonNull Account account);

    @NonNull
    CompletableFuture<Void> pullRemoteChanges(@NonNull Account account, @NonNull TParentEntity parentEntity, @Nullable SyncStatusReporter reporter);
}
