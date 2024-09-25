package it.niedermann.nextcloud.tables.types.descriptors.selection;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.selection.SelectionDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.selection.SelectionEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.selection.SelectionFactory;

public class SelectionDescriptor extends DataTypeDescriptor {

    public SelectionDescriptor() {
        this(new SelectionDefaultSupplier());
    }

    private SelectionDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new SelectionFactory(defaultValueSupplier), new SelectionEditorFactory(defaultValueSupplier));
    }

    private SelectionDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory) {
        super(viewHolderFactory, editorFactory);
    }
}
