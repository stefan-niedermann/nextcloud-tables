package it.niedermann.nextcloud.tables.features.row.editor.factories.text;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditRichBinding;
import it.niedermann.nextcloud.tables.features.row.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;
import it.niedermann.nextcloud.tables.features.row.editor.type.text.TextRichEditor;

public class TextRichEditorFactory implements EditorFactory<EditRichBinding> {

    @NonNull
    @Override
    public DataEditView<EditRichBinding> create(@NonNull Account account, @NonNull Context context,
                                                @NonNull FullColumn fullColumn,
                                                @Nullable FragmentManager fragmentManager) {
        return new TextRichEditor(context, fullColumn.getColumn());
    }
}
