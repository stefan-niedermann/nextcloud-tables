package it.niedermann.nextcloud.tables.ui.column.edit.factories.number;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageNumberBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.column.edit.types.number.NumberManager;

public class NumberManagerFactory implements ManageFactory<ManageNumberBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageNumberBinding> create(@NonNull Context context,
                                                      @Nullable FragmentManager fragmentManager) {
        return new NumberManager(context, fragmentManager);
    }
}
