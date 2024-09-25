package it.niedermann.nextcloud.tables.types.descriptors.text;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.text.TextDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.text.TextRichEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.text.RichViewFactory;

public class RichViewDescriptor extends DataTypeDescriptor {

    public RichViewDescriptor() {
        this(new TextDefaultSupplier());
    }

    private RichViewDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new RichViewFactory(defaultValueSupplier), new TextRichEditorFactory(defaultValueSupplier));
    }

    private RichViewDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory) {
        super(viewHolderFactory, editorFactory);
    }
}
