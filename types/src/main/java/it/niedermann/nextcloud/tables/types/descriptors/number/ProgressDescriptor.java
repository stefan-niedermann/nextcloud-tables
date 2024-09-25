package it.niedermann.nextcloud.tables.types.descriptors.number;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.number.NumberDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.number.NumberProgressEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.number.ProgressCellFactory;

public class ProgressDescriptor extends DataTypeDescriptor {

    public ProgressDescriptor() {
        this(new NumberDefaultSupplier());
    }

    private ProgressDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new ProgressCellFactory(defaultValueSupplier), new NumberProgressEditorFactory(defaultValueSupplier));
    }

    private ProgressDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory) {
        super(viewHolderFactory, editorFactory);
    }
}
