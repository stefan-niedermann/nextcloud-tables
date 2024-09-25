package it.niedermann.nextcloud.tables.types.descriptors.datetime;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.datetime.DateTimeDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.datetime.DateTimeEditorFactory;
import it.niedermann.nextcloud.tables.types.interceptors.Interceptor;
import it.niedermann.nextcloud.tables.types.interceptors.datetime.DateTimeInterceptor;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.datetime.DateTimeCellFactory;

public class DateTimeDescriptor extends DataTypeDescriptor {

    public DateTimeDescriptor() {
        this(new DateTimeDefaultSupplier());
    }

    private DateTimeDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new DateTimeCellFactory(defaultValueSupplier),
                new DateTimeEditorFactory(defaultValueSupplier),
                new DateTimeInterceptor());
    }

    private DateTimeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull Interceptor interceptor) {
        super(viewHolderFactory, editorFactory, interceptor);
    }
}
