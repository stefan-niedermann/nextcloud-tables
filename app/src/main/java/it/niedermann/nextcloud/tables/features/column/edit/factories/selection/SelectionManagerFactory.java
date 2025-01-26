package it.niedermann.nextcloud.tables.features.column.edit.factories.selection;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageSelectionCheckBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.selection.SelectionCheckManager;

public class SelectionManagerFactory implements ManageFactory<ManageSelectionCheckBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageSelectionCheckBinding> create(@NonNull Context context,
                                                              @Nullable FragmentManager fragmentManager) {
        return new SelectionCheckManager(context, fragmentManager);
    }
}
