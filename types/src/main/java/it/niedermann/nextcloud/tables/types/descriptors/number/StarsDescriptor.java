package it.niedermann.nextcloud.tables.types.descriptors.number;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.number.NumberDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.number.NumberStarsEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.number.StarsCellFactory;

public class StarsDescriptor extends DataTypeDescriptor {

    public StarsDescriptor() {
        this(new NumberDefaultSupplier());
    }

    private StarsDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new StarsCellFactory(defaultValueSupplier), new NumberStarsEditorFactory(defaultValueSupplier));
    }

    private StarsDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory) {
        super(viewHolderFactory, editorFactory);
    }
}
