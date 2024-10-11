package it.niedermann.nextcloud.tables.ui.table.view.types;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.datetime.DateCellFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.datetime.DateTimeCellFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.datetime.TimeCellFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.number.NumberCellFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.number.ProgressCellFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.number.StarsCellFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.selection.SelectionCheckFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.selection.SelectionFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.selection.SelectionMultiFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.text.LongCellFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.text.RichViewFactory;
import it.niedermann.nextcloud.tables.ui.table.view.types.factories.text.TextCellFactory;

public class DataTypeViewerServiceRegistry extends DataTypeServiceRegistry<ViewHolderFactory> {

    private final DataTypeServiceRegistry<DefaultValueSupplier> defaultSupplierServiceRegistry;

    public DataTypeViewerServiceRegistry(
            @NonNull DataTypeServiceRegistry<DefaultValueSupplier> defaultSupplierServiceRegistry
    ) {
        super(true);
        this.defaultSupplierServiceRegistry = defaultSupplierServiceRegistry;
        warmUp();
    }

    @Override
    public ViewHolderFactory getService(@NonNull EDataType dataType) {
        return switch (dataType) {
            case TEXT, TEXT_LINK, TEXT_LINE, USERGROUP, UNKNOWN ->
                    cache.computeIfAbsent(dataType, t -> new TextCellFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case TEXT_LONG ->
                    cache.computeIfAbsent(dataType, t -> new LongCellFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case TEXT_RICH ->
                    cache.computeIfAbsent(dataType, t -> new RichViewFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case DATETIME, DATETIME_DATETIME ->
                    cache.computeIfAbsent(dataType, t -> new DateTimeCellFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case DATETIME_DATE ->
                    cache.computeIfAbsent(dataType, t -> new DateCellFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case DATETIME_TIME ->
                    cache.computeIfAbsent(dataType, t -> new TimeCellFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case SELECTION ->
                    cache.computeIfAbsent(dataType, t -> new SelectionFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case SELECTION_MULTI ->
                    cache.computeIfAbsent(dataType, t -> new SelectionMultiFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case SELECTION_CHECK ->
                    cache.computeIfAbsent(dataType, t -> new SelectionCheckFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case NUMBER ->
                    cache.computeIfAbsent(dataType, t -> new NumberCellFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case NUMBER_PROGRESS ->
                    cache.computeIfAbsent(dataType, t -> new ProgressCellFactory(defaultSupplierServiceRegistry.getService(dataType)));
            case NUMBER_STARS ->
                    cache.computeIfAbsent(dataType, t -> new StarsCellFactory(defaultSupplierServiceRegistry.getService(dataType)));
        };
    }
}
