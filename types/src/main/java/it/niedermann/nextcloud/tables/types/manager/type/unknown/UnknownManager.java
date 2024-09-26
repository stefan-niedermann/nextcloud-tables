package it.niedermann.nextcloud.tables.types.manager.type.unknown;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.ManageUnknownBinding;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;

public class UnknownManager extends ColumnManageView {

    protected ManageUnknownBinding binding;

    public UnknownManager(@NonNull Context context) {
        super(context);
    }

    public UnknownManager(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
    }

    public UnknownManager(@NonNull Context context,
                          @NonNull Column column,
                          @Nullable FragmentManager fragmentManager) {
        super(context, column, fragmentManager);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        return ManageUnknownBinding.inflate(LayoutInflater.from(context)).getRoot();
    }
}
