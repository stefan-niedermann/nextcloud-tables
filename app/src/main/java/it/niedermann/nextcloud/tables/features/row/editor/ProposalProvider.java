package it.niedermann.nextcloud.tables.features.row.editor;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.Collection;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;

@FunctionalInterface
public interface ProposalProvider<ProposalType> {

    @NonNull
    LiveData<Collection<ProposalType>> getProposals(@NonNull Account account,
                                                    @NonNull Column column,
                                                    @NonNull String term);
}