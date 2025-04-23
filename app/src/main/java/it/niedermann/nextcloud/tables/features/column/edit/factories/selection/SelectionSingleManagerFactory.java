package it.niedermann.nextcloud.tables.features.column.edit.factories.selection;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageSelectionSingleBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.selection.SelectionSingleManager;

public class SelectionSingleManagerFactory implements ManageFactory<ManageSelectionSingleBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageSelectionSingleBinding> create(@NonNull Context context,
                                                               @Nullable FragmentManager fragmentManager) {
        return new SelectionSingleManager(context, fragmentManager);
    }
}
