package it.niedermann.nextcloud.tables.types.editor.factories;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public abstract class AbstractEditorFactory implements EditorFactory {

    @NonNull
    protected final DefaultValueSupplier defaultValueSupplier;

    protected AbstractEditorFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
    }
}
