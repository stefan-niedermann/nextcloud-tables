package it.niedermann.nextcloud.tables.features.row.editor.type;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;
import it.niedermann.nextcloud.tables.features.row.editor.type.datetime.DateTimeDateEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.datetime.DateTimeEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.datetime.DateTimeTimeEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.number.NumberEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.number.NumberProgressEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.number.NumberStarsEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.selection.SelectionCheckEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.selection.SelectionEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.selection.SelectionMultiEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.text.TextLineEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.text.TextLinkEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.text.TextRichEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.unknown.UnknownEditor;
import it.niedermann.nextcloud.tables.features.row.editor.type.usergroup.UserGroupEditor;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResultEntry;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;

public class DataEditViewFactory {

    private final Context context;
    private final FragmentManager fragmentManager;
    private final ProposalProvider<Pair<SearchProvider, OcsSearchResultEntry>> searchProposalProvider;
    private final ProposalProvider<OcsAutocompleteResult> autocompleteProposalProvider;

    public DataEditViewFactory(
            @NonNull Context context,
            @NonNull FragmentManager fragmentManager,
            @NonNull ProposalProvider<Pair<SearchProvider, OcsSearchResultEntry>> searchProposalProvider,
            @NonNull ProposalProvider<OcsAutocompleteResult> autocompleteProposalProvider) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.searchProposalProvider = searchProposalProvider;
        this.autocompleteProposalProvider = autocompleteProposalProvider;
    }

    public DataEditView<? extends ViewBinding> createDataEditView(@NonNull EDataType dataType,
                                                                  @NonNull Account account,
                                                                  @NonNull FullColumn fullColumn) {
        return switch (dataType) {
            case TEXT_LONG, TEXT_RICH -> new TextRichEditor(context, fullColumn.getColumn());
            case TEXT_LINE -> new TextLineEditor(context, fullColumn.getColumn());
            case TEXT_LINK ->
                    new TextLinkEditor(account, context, fullColumn.getColumn(), searchProposalProvider);
            case DATETIME -> new DateTimeEditor(context, fragmentManager, fullColumn.getColumn());
            case DATETIME_DATE ->
                    new DateTimeDateEditor(context, fragmentManager, fullColumn.getColumn());
            case DATETIME_TIME ->
                    new DateTimeTimeEditor(context, fragmentManager, fullColumn.getColumn());
            case SELECTION -> new SelectionEditor(context, fullColumn);
            case SELECTION_CHECK -> new SelectionCheckEditor(context, fullColumn.getColumn());
            case SELECTION_MULTI -> new SelectionMultiEditor(context, fullColumn);
            case NUMBER -> new NumberEditor(context, fullColumn.getColumn(), fragmentManager);
            case NUMBER_PROGRESS -> new NumberProgressEditor(context, fullColumn.getColumn());
            case NUMBER_STARS -> new NumberStarsEditor(context, fullColumn.getColumn());
            case USERGROUP -> FeatureToggle.EDIT_USER_GROUPS.enabled
                    ? new UserGroupEditor(account, context, fullColumn.getColumn(), autocompleteProposalProvider)
                    : new UnknownEditor(context, fragmentManager, fullColumn.getColumn());
            case UNKNOWN -> new UnknownEditor(context, fragmentManager, fullColumn.getColumn());
        };
    }
}
