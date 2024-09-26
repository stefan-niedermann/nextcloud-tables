package it.niedermann.nextcloud.tables.types.editor.factories.selection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.factories.AbstractEditorFactory;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;
import it.niedermann.nextcloud.tables.types.editor.type.selection.SelectionEditor;

public class SelectionEditorFactory extends AbstractEditorFactory {

    public SelectionEditorFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        super(defaultValueSupplier);
    }

    @NonNull
    @Override
    public ColumnEditView create(@NonNull Context context,
                                 @NonNull Column column,
                                 @Nullable Data data,
                                 @Nullable FragmentManager fragmentManager) throws Exception {
        return new SelectionEditor(context, column, data, defaultValueSupplier);
    }
}
