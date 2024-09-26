package it.niedermann.nextcloud.tables.types.manager.factories.number;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.manager.factories.ManageFactory;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;
import it.niedermann.nextcloud.tables.types.manager.type.number.NumberManager;

public class NumberManagerFactory implements ManageFactory {

    @NonNull
    @Override
    public ColumnManageView create(@NonNull Context context,
                                   @NonNull Column column,
                                   @Nullable FragmentManager fragmentManager) {
        return new NumberManager(context, column, fragmentManager);
    }
}
