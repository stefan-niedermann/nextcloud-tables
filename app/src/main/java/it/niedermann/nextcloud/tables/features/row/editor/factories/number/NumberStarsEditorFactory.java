package it.niedermann.nextcloud.tables.features.row.editor.factories.number;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditNumberStarsBinding;
import it.niedermann.nextcloud.tables.features.row.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;
import it.niedermann.nextcloud.tables.features.row.editor.type.number.NumberStarsEditor;

public class NumberStarsEditorFactory implements EditorFactory<EditNumberStarsBinding> {

    @NonNull
    @Override
    public DataEditView<EditNumberStarsBinding> create(@NonNull Account account, @NonNull Context context,
                                                       @NonNull FullColumn fullColumn,
                                                       @Nullable FragmentManager fragmentManager) {
        return new NumberStarsEditor(context, fullColumn.getColumn());
    }
}
