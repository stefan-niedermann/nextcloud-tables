package it.niedermann.nextcloud.tables.features.column.edit.factories.text;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageTextLineBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.text.TextLineManager;

public class TextLineManagerFactory implements ManageFactory<ManageTextLineBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageTextLineBinding> create(@NonNull Context context,
                                                        @Nullable FragmentManager fragmentManager) {
        return new TextLineManager(context, fragmentManager);
    }
}
