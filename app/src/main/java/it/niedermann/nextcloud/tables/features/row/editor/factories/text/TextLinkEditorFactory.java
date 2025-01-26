package it.niedermann.nextcloud.tables.features.row.editor.factories.text;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import java.util.Collection;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditTextLinkBinding;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;
import it.niedermann.nextcloud.tables.features.row.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditViewWithProposalProvider;
import it.niedermann.nextcloud.tables.features.row.editor.type.text.TextLinkEditor;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResultEntry;

public class TextLinkEditorFactory implements EditorFactory<EditTextLinkBinding> {

    @NonNull
    private final ProposalProvider<Collection<Pair<SearchProvider, OcsSearchResultEntry>>> proposalProvider;

    public TextLinkEditorFactory(@NonNull ProposalProvider<Collection<Pair<SearchProvider, OcsSearchResultEntry>>> proposalProvider) {
        this.proposalProvider = proposalProvider;
    }

    @NonNull
    @Override
    public DataEditViewWithProposalProvider<EditTextLinkBinding, Collection<Pair<SearchProvider, OcsSearchResultEntry>>> create(@NonNull Account account,
                                                                                                                                @NonNull Context context,
                                                                                                                                @NonNull FullColumn fullColumn,
                                                                                                                                @Nullable FragmentManager fragmentManager) {
        return new TextLinkEditor(account, context, fullColumn.getColumn(), proposalProvider);
    }
}
