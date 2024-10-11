package it.niedermann.nextcloud.tables.ui.column.edit.factories;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;

public interface ManageFactory<T extends ViewBinding> {
    @NonNull
    ColumnEditView<T> create(@NonNull Context context,
                             @Nullable FragmentManager fragmentManager);
}
