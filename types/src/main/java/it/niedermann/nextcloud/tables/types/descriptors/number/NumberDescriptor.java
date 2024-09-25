package it.niedermann.nextcloud.tables.types.descriptors.number;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.number.NumberDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.number.NumberEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.number.NumberCellFactory;

public class NumberDescriptor extends DataTypeDescriptor {

    public NumberDescriptor() {
        this(new NumberDefaultSupplier());
    }

    private NumberDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new NumberCellFactory(defaultValueSupplier), new NumberEditorFactory(defaultValueSupplier));
    }

    private NumberDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory) {
        super(viewHolderFactory, editorFactory);
    }
}
