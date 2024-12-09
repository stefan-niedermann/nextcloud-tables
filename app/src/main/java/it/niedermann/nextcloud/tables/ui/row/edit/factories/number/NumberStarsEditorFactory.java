package it.niedermann.nextcloud.tables.ui.row.edit.factories.number;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditNumberStarsBinding;
import it.niedermann.nextcloud.tables.ui.row.edit.factories.EditorFactory;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;
import it.niedermann.nextcloud.tables.ui.row.edit.type.number.NumberStarsEditor;

public class NumberStarsEditorFactory implements EditorFactory<EditNumberStarsBinding> {

    @NonNull
    @Override
    public DataEditView<EditNumberStarsBinding> create(@NonNull Context context,
                                                       @NonNull FullColumn fullColumn,
                                                       @Nullable FragmentManager fragmentManager) {
        return new NumberStarsEditor(context, fullColumn.getColumn());
    }
}
