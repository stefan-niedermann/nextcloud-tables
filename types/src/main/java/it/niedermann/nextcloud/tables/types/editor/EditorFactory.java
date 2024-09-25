package it.niedermann.nextcloud.tables.types.editor;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;

public interface EditorFactory {
    @NonNull
    ColumnEditView create(@NonNull Context context,
                          @NonNull Column column,
                          @Nullable Data data,
                          @Nullable FragmentManager fragmentManager) throws Exception;
}
