package it.niedermann.nextcloud.tables.types.manager.factories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;

public interface ManageFactory {
    @NonNull
    ColumnManageView create(@NonNull Context context,
                            @NonNull Column column,
                            @Nullable FragmentManager fragmentManager);
}
