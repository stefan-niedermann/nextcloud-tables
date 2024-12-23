package it.niedermann.nextcloud.tables.features.column.edit.factories.datetime;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageDatetimeBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.datetime.DateTimeManager;

public class DateTimeManagerFactory implements ManageFactory<ManageDatetimeBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageDatetimeBinding> create(@NonNull Context context,
                                                        @Nullable FragmentManager fragmentManager) {
        return new DateTimeManager(context, fragmentManager);
    }
}
