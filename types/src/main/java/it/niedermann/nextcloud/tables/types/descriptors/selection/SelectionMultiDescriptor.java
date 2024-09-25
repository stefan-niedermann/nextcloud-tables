package it.niedermann.nextcloud.tables.types.descriptors.selection;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.selection.SelectionDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.selection.SelectionMultiEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.selection.SelectionMultiFactory;

public class SelectionMultiDescriptor extends DataTypeDescriptor {

    public SelectionMultiDescriptor() {
        this(new SelectionDefaultSupplier());
    }

    private SelectionMultiDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new SelectionMultiFactory(defaultValueSupplier), new SelectionMultiEditorFactory(defaultValueSupplier));
    }

    private SelectionMultiDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory) {
        super(viewHolderFactory, editorFactory);
    }
}
