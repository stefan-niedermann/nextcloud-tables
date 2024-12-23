package it.niedermann.nextcloud.tables.features.column.edit.factories.datetime;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageDateBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.datetime.DateManager;

public class DateManagerFactory implements ManageFactory<ManageDateBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageDateBinding> create(@NonNull Context context,
                                                    @Nullable FragmentManager fragmentManager) {
        return new DateManager(context, fragmentManager);
    }
}
