package it.niedermann.nextcloud.tables.ui.row.edit.factories.number;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditTextviewBinding;
import it.niedermann.nextcloud.tables.ui.row.edit.factories.EditorFactory;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;
import it.niedermann.nextcloud.tables.ui.row.edit.type.number.NumberEditor;

public class NumberEditorFactory implements EditorFactory<EditTextviewBinding> {

    @NonNull
    @Override
    public DataEditView<EditTextviewBinding> create(@NonNull Context context,
                                                    @NonNull FullColumn fullColumn,
                                                    @Nullable FragmentManager fragmentManager) {
        return new NumberEditor(context, fullColumn.getColumn(), fragmentManager);
    }
}
