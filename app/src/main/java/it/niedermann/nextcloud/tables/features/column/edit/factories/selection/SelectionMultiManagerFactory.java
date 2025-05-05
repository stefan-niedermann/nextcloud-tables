package it.niedermann.nextcloud.tables.features.column.edit.factories.selection;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageSelectionMultiBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.selection.SelectionMultiManager;

public class SelectionMultiManagerFactory implements ManageFactory<ManageSelectionMultiBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageSelectionMultiBinding> create(@NonNull Context context,
                                                              @Nullable FragmentManager fragmentManager) {
        return new SelectionMultiManager(context, fragmentManager);
    }
}
