package it.niedermann.nextcloud.tables.features.column.edit.factories.usergroup;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageUsergroupBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.usergroup.UserGroupManager;

public class UserGroupManagerFactory implements ManageFactory<ManageUsergroupBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageUsergroupBinding> create(@NonNull Context context,
                                                              @Nullable FragmentManager fragmentManager) {
        return new UserGroupManager(context, fragmentManager);
    }
}
