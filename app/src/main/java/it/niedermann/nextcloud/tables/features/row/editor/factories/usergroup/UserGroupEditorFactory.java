package it.niedermann.nextcloud.tables.features.row.editor.factories.usergroup;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditAutocompleteBinding;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;
import it.niedermann.nextcloud.tables.features.row.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.type.AutocompleteEditView;
import it.niedermann.nextcloud.tables.features.row.editor.type.usergroup.UserGroupEditor;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;

public class UserGroupEditorFactory implements EditorFactory<EditAutocompleteBinding> {

    @NonNull
    private final ProposalProvider<OcsAutocompleteResult> proposalProvider;

    public UserGroupEditorFactory(@NonNull ProposalProvider<OcsAutocompleteResult> proposalProvider) {
        this.proposalProvider = proposalProvider;
    }

    @NonNull
    @Override
    public AutocompleteEditView<OcsAutocompleteResult> create(@NonNull Account account,
                                                              @NonNull Context context,
                                                              @NonNull FullColumn fullColumn,
                                                              @Nullable FragmentManager fragmentManager) {
        return new UserGroupEditor(account, context, fullColumn.getColumn(), proposalProvider);
    }
}
