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
import it.niedermann.nextcloud.tables.features.column.edit.factories.selection.SelectionManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.text.TextManagerFactory;
import it.niedermann.nextcloud.tables.features.column.edit.factories.unknown.UnknownManagerFactory;

public class ManageDataTypeServiceRegistry extends DataTypeServiceRegistry<ManageFactory<? extends ViewBinding>> {

    public ManageDataTypeServiceRegistry() {
        super();
    }

    @Override
    public ManageFactory<? extends ViewBinding> getService(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT_LONG, TEXT_LINE, TEXT_LINK, TEXT_RICH ->
                    cache.computeIfAbsent(dataType, t -> new TextManagerFactory());
            case DATETIME, DATETIME_TIME ->
                    cache.computeIfAbsent(dataType, t -> new DateTimeManagerFactory());
            case DATETIME_DATE -> cache.computeIfAbsent(dataType, t -> new DateManagerFactory());
            case SELECTION, SELECTION_MULTI, SELECTION_CHECK ->
                    cache.computeIfAbsent(dataType, t -> new SelectionManagerFactory());
            case NUMBER -> cache.computeIfAbsent(dataType, t -> new NumberManagerFactory());
            case NUMBER_PROGRESS ->
                    cache.computeIfAbsent(dataType, t -> new NumberProgressManagerFactory());
            case NUMBER_STARS ->
                    cache.computeIfAbsent(dataType, t -> new NumberStarsManagerFactory());
            case USERGROUP, UNKNOWN ->
                    cache.computeIfAbsent(dataType, t -> new UnknownManagerFactory());
        };
    }
}
