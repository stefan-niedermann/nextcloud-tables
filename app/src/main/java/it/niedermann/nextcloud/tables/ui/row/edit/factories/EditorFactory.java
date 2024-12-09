package it.niedermann.nextcloud.tables.ui.row.edit.factories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;

public interface EditorFactory<T extends ViewBinding> {

    @NonNull
    DataEditView<T> create(@NonNull Context context,
                           @NonNull FullColumn fullColumn,
                           @Nullable FragmentManager fragmentManager);
}
