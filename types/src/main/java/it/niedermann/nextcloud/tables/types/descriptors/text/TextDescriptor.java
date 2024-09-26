package it.niedermann.nextcloud.tables.types.descriptors.text;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.creators.type.TextCreator;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.text.TextDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.text.TextEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.text.TextCellFactory;

public class TextDescriptor extends DataTypeDescriptor {

    public TextDescriptor() {
        this(new TextDefaultSupplier());
    }

    private TextDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new TextCellFactory(defaultValueSupplier),
                new TextEditorFactory(defaultValueSupplier),
                new TextCreator());
    }

    private TextDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator) {
        super(viewHolderFactory, editorFactory, columnCreator);
    }
}
