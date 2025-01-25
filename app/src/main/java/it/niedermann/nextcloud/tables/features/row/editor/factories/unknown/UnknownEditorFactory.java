package it.niedermann.nextcloud.tables.features.row.editor.factories.unknown;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditTextviewBinding;
import it.niedermann.nextcloud.tables.features.row.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;
import it.niedermann.nextcloud.tables.features.row.editor.type.unknown.UnknownEditor;

public class UnknownEditorFactory implements EditorFactory<EditTextviewBinding> {

    @NonNull
    @Override
    public DataEditView<EditTextviewBinding> create(@NonNull Account account, @NonNull Context context,
                                                    @NonNull FullColumn fullColumn,
                                                    @Nullable FragmentManager fragmentManager) {
        return new UnknownEditor(context, fragmentManager, fullColumn.getColumn());
    }
}
