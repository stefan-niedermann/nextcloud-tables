package it.niedermann.nextcloud.tables.types.manager.type.datetime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.ManageDatetimeBinding;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;

public class DateTimeManager extends ColumnManageView {

    private static final String NOW = "now";
    protected ManageDatetimeBinding binding;

    public DateTimeManager(@NonNull Context context) {
        super(context);
    }

    public DateTimeManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimeManager(@NonNull Context context, @NonNull Column column, @Nullable FragmentManager fragmentManager) {
        super(context, column, fragmentManager);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = ManageDatetimeBinding.inflate(LayoutInflater.from(context));
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Column getColumn() {
        column.setDatetimeDefault(binding.useNowAsDefault.isChecked() ? NOW : "");
        return super.getColumn();
    }

    @Override
    protected void setColumn(@NonNull Column column) {
        super.setColumn(column);
        binding.useNowAsDefault.setChecked(NOW.equals(column.getDatetimeDefault()));
    }
}
