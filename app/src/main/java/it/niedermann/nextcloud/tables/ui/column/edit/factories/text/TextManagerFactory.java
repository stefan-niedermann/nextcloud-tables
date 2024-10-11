package it.niedermann.nextcloud.tables.ui.column.edit.factories.text;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageTextBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.column.edit.types.text.TextManager;

public class TextManagerFactory implements ManageFactory<ManageTextBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageTextBinding> create(@NonNull Context context,
                                                    @Nullable FragmentManager fragmentManager) {
        return new TextManager(context, fragmentManager);
    }
}
