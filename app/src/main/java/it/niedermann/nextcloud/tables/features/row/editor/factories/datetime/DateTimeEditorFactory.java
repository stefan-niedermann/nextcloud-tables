package it.niedermann.nextcloud.tables.features.row.editor.factories.datetime;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditDatetimeBinding;
import it.niedermann.nextcloud.tables.features.row.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;
import it.niedermann.nextcloud.tables.features.row.editor.type.datetime.DateTimeEditor;

public class DateTimeEditorFactory implements EditorFactory<EditDatetimeBinding> {

    @NonNull
    @Override
    public DataEditView<EditDatetimeBinding> create(@NonNull Account account, @NonNull Context context,
                                                    @NonNull FullColumn fullColumn,
                                                    @Nullable FragmentManager fragmentManager) {
        return new DateTimeEditor(context, fragmentManager, fullColumn.getColumn());
    }
}
