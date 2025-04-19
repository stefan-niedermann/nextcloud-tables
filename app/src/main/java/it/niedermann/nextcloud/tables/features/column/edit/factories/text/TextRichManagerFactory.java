package it.niedermann.nextcloud.tables.features.column.edit.factories.text;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageTextRichBinding;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.text.TextRichManager;

public class TextRichManagerFactory implements ManageFactory<ManageTextRichBinding> {

    @NonNull
    @Override
    public ColumnEditView<ManageTextRichBinding> create(@NonNull Context context,
                                                    @Nullable FragmentManager fragmentManager) {
        return new TextRichManager(context, fragmentManager);
    }
}
