package it.niedermann.nextcloud.tables.features.column.edit.types.unknown;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.databinding.ManageUnknownBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;

public class UnknownManager extends ColumnEditView<ManageUnknownBinding> {

    public UnknownManager(@NonNull Context context) {
        super(context);
    }

    public UnknownManager(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public UnknownManager(@NonNull Context context,
                          @Nullable FragmentManager fragmentManager) {
        super(context, ManageUnknownBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }
}
