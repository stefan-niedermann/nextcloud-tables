package it.niedermann.nextcloud.tables.features.row.editor.factories.selection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditSelectionCheckBinding;
import it.niedermann.nextcloud.tables.features.row.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;
import it.niedermann.nextcloud.tables.features.row.editor.type.selection.SelectionCheckEditor;

public class SelectionCheckEditorFactory implements EditorFactory<EditSelectionCheckBinding> {

    @NonNull
    @Override
    public DataEditView<EditSelectionCheckBinding> create(@NonNull Account account, @NonNull Context context,
                                                          @NonNull FullColumn fullColumn,
                                                          @Nullable FragmentManager fragmentManager) {
        return new SelectionCheckEditor(context, fullColumn.getColumn());
    }
}
