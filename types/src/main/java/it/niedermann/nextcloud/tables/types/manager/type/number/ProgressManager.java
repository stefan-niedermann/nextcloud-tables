package it.niedermann.nextcloud.tables.types.manager.type.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.ManageNumberProgressBinding;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;

public class ProgressManager extends ColumnManageView {

    protected ManageNumberProgressBinding binding;

    public ProgressManager(@NonNull Context context) {
        super(context);
    }

    public ProgressManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressManager(@NonNull Context context, @NonNull Column column, @Nullable FragmentManager fragmentManager) {
        super(context, column, fragmentManager);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = ManageNumberProgressBinding.inflate(LayoutInflater.from(context));
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Column getColumn() {
        try {
            column.setNumberDefault((double) binding.progress.getValue());
        } catch (NumberFormatException ignored) {

        }
        return super.getColumn();
    }

    @Override
    protected void setColumn(@NonNull Column column) {
        super.setColumn(column);
        final var value = column.getNumberDefault();
        // https://github.com/nextcloud/tables/issues/1385
        binding.progress.setValue(Objects.requireNonNullElse(value, 0).floatValue());
    }
}
