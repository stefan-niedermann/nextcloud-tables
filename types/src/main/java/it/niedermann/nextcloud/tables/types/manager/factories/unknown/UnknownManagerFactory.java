package it.niedermann.nextcloud.tables.types.manager.factories.unknown;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.manager.factories.ManageFactory;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;
import it.niedermann.nextcloud.tables.types.manager.type.unknown.UnknownManager;

public class UnknownManagerFactory implements ManageFactory {

    @NonNull
    @Override
    public ColumnManageView create(@NonNull Context context,
                                   @NonNull Column column,
                                   @Nullable FragmentManager fragmentManager) {
        return new UnknownManager(context, column, fragmentManager);
    }
}
