package it.niedermann.nextcloud.tables.types.descriptors.datetime;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.datetime.TimeDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.datetime.TimeEditorFactory;
import it.niedermann.nextcloud.tables.types.interceptors.Interceptor;
import it.niedermann.nextcloud.tables.types.interceptors.datetime.TimeInterceptor;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.datetime.TimeCellFactory;

public class TimeDescriptor extends DataTypeDescriptor {

    public TimeDescriptor() {
        this(new TimeDefaultSupplier());
    }

    private TimeDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new TimeCellFactory(defaultValueSupplier),
                new TimeEditorFactory(defaultValueSupplier),
                new TimeInterceptor());
    }

    private TimeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull Interceptor interceptor) {
        super(viewHolderFactory, editorFactory, interceptor);
    }
}
