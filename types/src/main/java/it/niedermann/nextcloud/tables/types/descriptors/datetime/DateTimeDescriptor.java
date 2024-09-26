package it.niedermann.nextcloud.tables.types.descriptors.datetime;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.creators.type.DateTimeCreator;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.datetime.DateTimeDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.datetime.DateTimeEditorFactory;
import it.niedermann.nextcloud.tables.types.interceptors.Interceptor;
import it.niedermann.nextcloud.tables.types.interceptors.datetime.DateTimeInterceptor;
import it.niedermann.nextcloud.tables.types.manager.factories.datetime.DateTimeManagerFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.datetime.DateTimeCellFactory;

public class DateTimeDescriptor extends DataTypeDescriptor {

    public DateTimeDescriptor() {
        this(new DateTimeDefaultSupplier());
    }

    private DateTimeDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new DateTimeCellFactory(defaultValueSupplier),
                new DateTimeEditorFactory(defaultValueSupplier),
                new DateTimeCreator(),
                new DateTimeInterceptor());
    }

    private DateTimeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator,
            @NonNull Interceptor interceptor) {
        super(viewHolderFactory, editorFactory, columnCreator, new DateTimeManagerFactory(), interceptor);
    }
}
