package it.niedermann.nextcloud.tables.types.descriptors.text;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.creators.type.TextCreator;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.text.TextDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.text.TextLinkEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.text.TextCellFactory;

public class LinkDescriptor extends DataTypeDescriptor {

    public LinkDescriptor() {
        this(new TextDefaultSupplier());
    }

    private LinkDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new TextCellFactory(defaultValueSupplier),
                new TextLinkEditorFactory(defaultValueSupplier),
                new TextCreator());
    }

    private LinkDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator) {
        super(viewHolderFactory, editorFactory, columnCreator);
    }
}
