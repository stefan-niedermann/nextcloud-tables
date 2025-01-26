package it.niedermann.nextcloud.tables.features.column.edit.factories.number;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageNumberProgressBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.number.ProgressManager;

public class NumberProgressManagerFactory implements ManageFactory<ManageNumberProgressBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageNumberProgressBinding> create(@NonNull Context context,
                                                              @Nullable FragmentManager fragmentManager) {
        return new ProgressManager(context, fragmentManager);
    }
}
