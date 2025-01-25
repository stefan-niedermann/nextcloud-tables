package it.niedermann.nextcloud.tables.features.row.editor.type;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;

public abstract class DataEditViewWithProposalProvider<ViewBindingType extends ViewBinding, ProposalProviderType> extends DataEditView<ViewBindingType> {

    protected final ProposalProvider<ProposalProviderType> proposalProvider;
    protected final Account account;

    public DataEditViewWithProposalProvider(@NonNull Context context,
                                            @NonNull ViewBindingType binding) {
        super(context, binding);
        proposalProvider = null;
        account = null;
    }

    public DataEditViewWithProposalProvider(@NonNull Context context,
                                            @Nullable AttributeSet attrs,
                                            @NonNull ViewBindingType binding) {
        super(context, attrs, binding);
        proposalProvider = null;
        account = null;
    }

    protected DataEditViewWithProposalProvider(@NonNull Account account,
                                               @NonNull Context context,
                                               @NonNull ViewBindingType binding,
                                               @NonNull Column column,
                                               @NonNull ProposalProvider<ProposalProviderType> proposalProvider) {
        this(account, context, binding, column, proposalProvider, null);
    }


    protected DataEditViewWithProposalProvider(@NonNull Account account,
                                               @NonNull Context context,
                                               @NonNull ViewBindingType binding,
                                               @NonNull Column column,
                                               @NonNull ProposalProvider<ProposalProviderType> proposalProvider,
                                               @Nullable FragmentManager fragmentManager) {
        super(context, binding, column, fragmentManager);
        this.proposalProvider = proposalProvider;
        this.account = account;
    }
}
