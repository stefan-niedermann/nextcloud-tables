package it.niedermann.nextcloud.tables.types.descriptors.unknown;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.creators.NoOpCreator;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.NoOpDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.unknown.UnknownEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.text.TextCellFactory;

public class UnknownDescriptor extends DataTypeDescriptor {

    public UnknownDescriptor() {
        this(new NoOpDefaultSupplier());
    }

    private UnknownDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new TextCellFactory(defaultValueSupplier),
                new UnknownEditorFactory(defaultValueSupplier),
                new NoOpCreator());
    }

    private UnknownDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator) {
        super(viewHolderFactory, editorFactory, columnCreator);
    }
}
