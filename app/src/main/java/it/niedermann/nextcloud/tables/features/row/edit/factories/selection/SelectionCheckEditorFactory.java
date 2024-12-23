package it.niedermann.nextcloud.tables.features.row.edit.factories.selection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditSelectionCheckBinding;
import it.niedermann.nextcloud.tables.features.row.edit.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.edit.type.DataEditView;
import it.niedermann.nextcloud.tables.features.row.edit.type.selection.SelectionCheckEditor;

public class SelectionCheckEditorFactory implements EditorFactory<EditSelectionCheckBinding> {

    @NonNull
    @Override
    public DataEditView<EditSelectionCheckBinding> create(@NonNull Context context,
                                                          @NonNull FullColumn fullColumn,
                                                          @Nullable FragmentManager fragmentManager) {
        return new SelectionCheckEditor(context, fullColumn.getColumn());
    }
}
