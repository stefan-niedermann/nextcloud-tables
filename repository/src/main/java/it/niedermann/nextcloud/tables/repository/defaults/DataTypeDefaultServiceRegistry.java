package it.niedermann.nextcloud.tables.repository.defaults;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.NoOpDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.datetime.DateDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.datetime.DateTimeDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.datetime.TimeDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.number.NumberDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.number.NumberStarsDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.selection.SelectionCheckDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.selection.SelectionDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.selection.SelectionMultiDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.text.TextDefaultSupplier;
import it.niedermann.nextcloud.tables.repository.defaults.supplier.usergroup.UserGroupDefaultSupplier;

public class DataTypeDefaultServiceRegistry extends DataTypeServiceRegistry<DefaultValueSupplier> {

    public DataTypeDefaultServiceRegistry() {
        super();
    }

    @Override
    public DefaultValueSupplier getService(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT, TEXT_LONG, TEXT_RICH, TEXT_LINE, TEXT_LINK ->
                    cache.computeIfAbsent(dataType, t -> new TextDefaultSupplier());
            case DATETIME, DATETIME_DATETIME ->
                    cache.computeIfAbsent(dataType, t -> new DateTimeDefaultSupplier());
            case DATETIME_DATE -> cache.computeIfAbsent(dataType, t -> new DateDefaultSupplier());
            case DATETIME_TIME -> cache.computeIfAbsent(dataType, t -> new TimeDefaultSupplier());
            case SELECTION -> cache.computeIfAbsent(dataType, t -> new SelectionDefaultSupplier());
            case SELECTION_MULTI ->
                    cache.computeIfAbsent(dataType, t -> new SelectionMultiDefaultSupplier());
            case SELECTION_CHECK ->
                    cache.computeIfAbsent(dataType, t -> new SelectionCheckDefaultSupplier());
            case NUMBER, NUMBER_PROGRESS ->
                    cache.computeIfAbsent(dataType, t -> new NumberDefaultSupplier());
            case NUMBER_STARS ->
                    cache.computeIfAbsent(dataType, t -> new NumberStarsDefaultSupplier());
            case USERGROUP -> cache.computeIfAbsent(dataType, t -> new UserGroupDefaultSupplier());
            case UNKNOWN -> cache.computeIfAbsent(dataType, t -> new NoOpDefaultSupplier());
        };
    }
}
