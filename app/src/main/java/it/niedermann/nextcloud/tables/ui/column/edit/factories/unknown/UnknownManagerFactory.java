package it.niedermann.nextcloud.tables.ui.column.edit.factories.unknown;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageUnknownBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.column.edit.types.unknown.UnknownManager;

public class UnknownManagerFactory implements ManageFactory<ManageUnknownBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageUnknownBinding> create(@NonNull Context context,
                                                       @Nullable FragmentManager fragmentManager) {
        return new UnknownManager(context, fragmentManager);
    }
}
