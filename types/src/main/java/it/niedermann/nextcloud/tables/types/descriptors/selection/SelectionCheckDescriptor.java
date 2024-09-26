package it.niedermann.nextcloud.tables.types.descriptors.selection;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.creators.type.SelectionCreator;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.selection.SelectionDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.selection.SelectionCheckEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.selection.SelectionCheckFactory;

public class SelectionCheckDescriptor extends DataTypeDescriptor {

    public SelectionCheckDescriptor() {
        this(new SelectionDefaultSupplier());
    }

    private SelectionCheckDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new SelectionCheckFactory(defaultValueSupplier),
                new SelectionCheckEditorFactory(defaultValueSupplier),
                new SelectionCreator());
    }

    private SelectionCheckDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator) {
        super(viewHolderFactory, editorFactory, columnCreator);
    }
}
