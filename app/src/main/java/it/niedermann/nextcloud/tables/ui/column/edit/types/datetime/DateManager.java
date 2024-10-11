package it.niedermann.nextcloud.tables.ui.column.edit.types.datetime;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.attributes.DateTimeAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageDateBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;

public class DateManager extends ColumnEditView<ManageDateBinding> {

    private static final String TODAY = "today";

    public DateManager(@NonNull Context context) {
        super(context);
    }

    public DateManager(@NonNull Context context,
                       @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateManager(@NonNull Context context,
                       @Nullable FragmentManager fragmentManager) {
        super(context, ManageDateBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        final var useTodayAsDefault = binding.useTodayAsDefault.isChecked();

        fullColumn.getColumn().getDefaultValue().setStringValue(useTodayAsDefault ? TODAY : null);
        fullColumn.getColumn().setDateTimeAttributes(new DateTimeAttributes());

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);
        binding.useTodayAsDefault.setChecked(TODAY.equals(fullColumn.getColumn().getDefaultValue().getStringValue()));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        binding.useTodayAsDefault.setEnabled(enabled);
    }
}
