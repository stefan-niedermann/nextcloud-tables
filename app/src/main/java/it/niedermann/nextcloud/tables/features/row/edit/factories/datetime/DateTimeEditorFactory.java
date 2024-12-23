package it.niedermann.nextcloud.tables.features.row.edit.factories.datetime;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.EditDatetimeBinding;
import it.niedermann.nextcloud.tables.features.row.edit.factories.EditorFactory;
import it.niedermann.nextcloud.tables.features.row.edit.type.DataEditView;
import it.niedermann.nextcloud.tables.features.row.edit.type.datetime.DateTimeEditor;

public class DateTimeEditorFactory implements EditorFactory<EditDatetimeBinding> {

    @NonNull
    @Override
    public DataEditView<EditDatetimeBinding> create(@NonNull Context context,
                                                    @NonNull FullColumn fullColumn,
                                                    @Nullable FragmentManager fragmentManager) {
        return new DateTimeEditor(context, fragmentManager, fullColumn.getColumn());
    }
}
