package it.niedermann.nextcloud.tables.types.descriptors.datetime;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.creators.type.DateTimeCreator;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.datetime.TimeDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.datetime.TimeEditorFactory;
import it.niedermann.nextcloud.tables.types.interceptors.Interceptor;
import it.niedermann.nextcloud.tables.types.interceptors.datetime.TimeInterceptor;
import it.niedermann.nextcloud.tables.types.manager.factories.unknown.UnknownManagerFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.datetime.TimeCellFactory;

public class TimeDescriptor extends DataTypeDescriptor {

    public TimeDescriptor() {
        this(new TimeDefaultSupplier());
    }

    private TimeDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new TimeCellFactory(defaultValueSupplier),
                new TimeEditorFactory(defaultValueSupplier),
                new DateTimeCreator(),
                new TimeInterceptor());
    }

    private TimeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator,
            @NonNull Interceptor interceptor) {
        super(viewHolderFactory, editorFactory, columnCreator, new UnknownManagerFactory(), interceptor);
    }
}
