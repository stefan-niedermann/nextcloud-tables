package it.niedermann.nextcloud.tables.types.editor.factories.text;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.factories.AbstractEditorFactory;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;
import it.niedermann.nextcloud.tables.types.editor.type.text.TextRichEditor;

public class TextRichEditorFactory extends AbstractEditorFactory {

    public TextRichEditorFactory(@NonNull DefaultValueSupplier defaultValueSupplier) {
        super(defaultValueSupplier);
    }

    @NonNull
    @Override
    public ColumnEditView create(@NonNull Context context,
                                 @NonNull Column column,
                                 @Nullable Data data,
                                 @Nullable FragmentManager fragmentManager) throws Exception {
        return new TextRichEditor(context, column, data, defaultValueSupplier);
    }
}
