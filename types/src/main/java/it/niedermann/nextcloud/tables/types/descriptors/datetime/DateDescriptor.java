package it.niedermann.nextcloud.tables.types.descriptors.datetime;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.datetime.DateDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.datetime.DateEditorFactory;
import it.niedermann.nextcloud.tables.types.interceptors.Interceptor;
import it.niedermann.nextcloud.tables.types.interceptors.datetime.DateInterceptor;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.datetime.DateCellFactory;

public class DateDescriptor extends DataTypeDescriptor {

    public DateDescriptor() {
        this(new DateDefaultSupplier());
    }

    private DateDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new DateCellFactory(defaultValueSupplier),
                new DateEditorFactory(defaultValueSupplier),
                new DateInterceptor());
    }

    private DateDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull Interceptor interceptor) {
        super(viewHolderFactory, editorFactory, interceptor);
    }
}
