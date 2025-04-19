package it.niedermann.nextcloud.tables.features.column.edit.factories.text;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageTextLinkBinding;
import it.niedermann.nextcloud.tables.features.column.edit.SearchProviderSupplier;
import it.niedermann.nextcloud.tables.features.column.edit.factories.ManageFactory;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.column.edit.types.text.TextLinkManager;

public class TextLinkManagerFactory implements ManageFactory<ManageTextLinkBinding> {

    private final SearchProviderSupplier searchProviderSupplier;

    public TextLinkManagerFactory(@NonNull SearchProviderSupplier searchProviderSupplier) {
        this.searchProviderSupplier = searchProviderSupplier;
    }

    @NonNull
    @Override
    public ColumnEditView<ManageTextLinkBinding> create(@NonNull Context context,
                                                        @Nullable FragmentManager fragmentManager) {
        return new TextLinkManager(context, searchProviderSupplier, fragmentManager);
    }
}
