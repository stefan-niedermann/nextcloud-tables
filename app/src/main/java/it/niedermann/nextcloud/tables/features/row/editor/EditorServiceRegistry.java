package it.niedermann.nextcloud.tables.features.row.editor;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.database.entity.SearchProvider;
import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.features.row.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.datetime.DateEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.datetime.DateTimeEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.datetime.TimeEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.number.NumberEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.number.NumberProgressEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.number.NumberStarsEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.selection.SelectionCheckEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.selection.SelectionEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.selection.SelectionMultiEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.text.TextLineEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.text.TextLinkEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.text.TextRichEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.unknown.UnknownEditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.factories.usergroup.UserGroupEditorFactory;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsSearchResultEntry;

public class EditorServiceRegistry extends DataTypeServiceRegistry<EditorFactory<? extends ViewBinding>> {

    private final ProposalProvider<Pair<SearchProvider, OcsSearchResultEntry>> searchProposalProvider;
    private final ProposalProvider<OcsAutocompleteResult> autocompleteProposalProvider;

    public EditorServiceRegistry(
            @NonNull ProposalProvider<Pair<SearchProvider, OcsSearchResultEntry>> searchProposalProvider,
            @NonNull ProposalProvider<OcsAutocompleteResult> autocompleteProposalProvider) {
        super(true);
        this.searchProposalProvider = searchProposalProvider;
        this.autocompleteProposalProvider = autocompleteProposalProvider;
    }

    @Override
    public EditorFactory<? extends ViewBinding> getService(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT_LONG, TEXT_RICH ->
                    cache.computeIfAbsent(dataType, t -> new TextRichEditorFactory());
            case TEXT_LINE -> cache.computeIfAbsent(dataType, t -> new TextLineEditorFactory());
            case TEXT_LINK ->
                    cache.computeIfAbsent(dataType, t -> new TextLinkEditorFactory(searchProposalProvider));

            case DATETIME -> cache.computeIfAbsent(dataType, t -> new DateTimeEditorFactory());
            case DATETIME_DATE -> cache.computeIfAbsent(dataType, t -> new DateEditorFactory());
            case DATETIME_TIME -> cache.computeIfAbsent(dataType, t -> new TimeEditorFactory());

            case SELECTION -> cache.computeIfAbsent(dataType, t -> new SelectionEditorFactory());
            case SELECTION_CHECK ->
                    cache.computeIfAbsent(dataType, t -> new SelectionCheckEditorFactory());
            case SELECTION_MULTI ->
                    cache.computeIfAbsent(dataType, t -> new SelectionMultiEditorFactory());

            case NUMBER -> cache.computeIfAbsent(dataType, t -> new NumberEditorFactory());
            case NUMBER_PROGRESS ->
                    cache.computeIfAbsent(dataType, t -> new NumberProgressEditorFactory());
            case NUMBER_STARS ->
                    cache.computeIfAbsent(dataType, t -> new NumberStarsEditorFactory());

            case USERGROUP ->
                    cache.computeIfAbsent(dataType, t -> new UserGroupEditorFactory(autocompleteProposalProvider));

            case UNKNOWN -> cache.computeIfAbsent(dataType, t -> new UnknownEditorFactory());
        };
    }
}
