package it.niedermann.nextcloud.tables.types.descriptors.text;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.creators.type.TextCreator;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.text.TextDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.text.TextLineEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.text.LineCellFactory;

public class LineDescriptor extends DataTypeDescriptor {

    public LineDescriptor() {
        this(new TextDefaultSupplier());
    }

    private LineDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new LineCellFactory(defaultValueSupplier),
                new TextLineEditorFactory(defaultValueSupplier),
                new TextCreator());
    }

    private LineDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator) {
        super(viewHolderFactory, editorFactory, columnCreator);
    }
}
