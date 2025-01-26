package it.niedermann.nextcloud.tables.features.row.editor.factories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;

public interface EditorFactory<ViewBindingType extends ViewBinding> {

    @NonNull
    DataEditView<ViewBindingType> create(@NonNull Account account, @NonNull Context context,
                                         @NonNull FullColumn fullColumn,
                                         @Nullable FragmentManager fragmentManager);
}
