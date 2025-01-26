package it.niedermann.nextcloud.tables.features.column.edit.factories.number;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageNumberStarsBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.number.StarsManager;

public class NumberStarsManagerFactory implements ManageFactory<ManageNumberStarsBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageNumberStarsBinding> create(@NonNull Context context,
                                                           @Nullable FragmentManager fragmentManager) {
        return new StarsManager(context, fragmentManager);
    }
}
