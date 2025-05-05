package it.niedermann.nextcloud.tables.features.column.edit;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.datetime.DateManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.datetime.DateTimeManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.number.NumberManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.number.NumberProgressManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.number.NumberStarsManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.selection.SelectionCheckManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.selection.SelectionMultiManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.selection.SelectionSingleManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.text.TextLineManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.text.TextLinkManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.text.TextRichManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.unknown.UnknownManagerFactory;

public class ManageDataTypeServiceRegistry extends DataTypeServiceRegistry<ManageFactory<? extends ViewBinding>> {

    private final SearchProviderSupplier searchProviderSupplier;

    public ManageDataTypeServiceRegistry(@NonNull SearchProviderSupplier searchProviderSupplier) {
        super(true);
        this.searchProviderSupplier = searchProviderSupplier;
    }

    @Override
    public ManageFactory<? extends ViewBinding> getService(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT_LINE -> cache.computeIfAbsent(dataType, t -> new TextLineManagerFactory());
            case TEXT_LINK ->
                    cache.computeIfAbsent(dataType, t -> new TextLinkManagerFactory(searchProviderSupplier));
            case TEXT_LONG, TEXT_RICH ->
                    cache.computeIfAbsent(dataType, t -> new TextRichManagerFactory());
            case DATETIME, DATETIME_TIME ->
                    cache.computeIfAbsent(dataType, t -> new DateTimeManagerFactory());
            case DATETIME_DATE -> cache.computeIfAbsent(dataType, t -> new DateManagerFactory());
            case SELECTION ->
                    cache.computeIfAbsent(dataType, t -> new SelectionSingleManagerFactory());
            case SELECTION_MULTI ->
                    cache.computeIfAbsent(dataType, t -> new SelectionMultiManagerFactory());
            case SELECTION_CHECK ->
                    cache.computeIfAbsent(dataType, t -> new SelectionCheckManagerFactory());
            case NUMBER -> cache.computeIfAbsent(dataType, t -> new NumberManagerFactory());
            case NUMBER_PROGRESS ->
                    cache.computeIfAbsent(dataType, t -> new NumberProgressManagerFactory());
            case NUMBER_STARS ->
                    cache.computeIfAbsent(dataType, t -> new NumberStarsManagerFactory());
//            case USERGROUP ->
//                    cache.computeIfAbsent(dataType, t -> new UserGroupManagerFactory());
            case USERGROUP, UNKNOWN ->
                    cache.computeIfAbsent(dataType, t -> new UnknownManagerFactory());
        };
    }
}
