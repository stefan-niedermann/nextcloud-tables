package it.niedermann.nextcloud.tables.features.column.edit.types;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.features.column.edit.SearchProviderSupplier;
import it.niedermann.nextcloud.tables.features.column.edit.types.datetime.DateManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.datetime.DateTimeManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.number.NumberManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.number.ProgressManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.number.StarsManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.selection.SelectionCheckManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.selection.SelectionMultiManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.selection.SelectionSingleManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.text.TextLineManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.text.TextLinkManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.text.TextRichManager;
import it.niedermann.nextcloud.tables.features.column.edit.types.unknown.UnknownManager;

public class ColumnEditViewFactory {

    private final Context context;
    private final FragmentManager fragmentManager;
    private final SearchProviderSupplier searchProviderSupplier;

    public ColumnEditViewFactory(
            @NonNull Context context,
            @NonNull FragmentManager fragmentManager,
            @NonNull SearchProviderSupplier searchProviderSupplier
    ) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.searchProviderSupplier = searchProviderSupplier;
    }

    public ColumnEditView<? extends ViewBinding> createColumnEditView(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT_LINE -> new TextLineManager(context, fragmentManager);
            case TEXT_LINK -> new TextLinkManager(context, searchProviderSupplier, fragmentManager);
            case TEXT_LONG, TEXT_RICH -> new TextRichManager(context, fragmentManager);
            case DATETIME, DATETIME_TIME -> new DateTimeManager(context, fragmentManager);
            case DATETIME_DATE -> new DateManager(context, fragmentManager);
            case SELECTION -> new SelectionSingleManager(context, fragmentManager);
            case SELECTION_MULTI -> new SelectionMultiManager(context, fragmentManager);
            case SELECTION_CHECK -> new SelectionCheckManager(context, fragmentManager);
            case NUMBER -> new NumberManager(context, fragmentManager);
            case NUMBER_PROGRESS -> new ProgressManager(context, fragmentManager);
            case NUMBER_STARS -> new StarsManager(context, fragmentManager);
            // case USERGROUP -> new UserGroupManager(context, fragmentManager);
            case USERGROUP, UNKNOWN -> new UnknownManager(context, fragmentManager);
        };
    }
}
