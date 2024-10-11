package it.niedermann.nextcloud.tables.ui.column.edit.types.datetime;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.attributes.DateTimeAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageDatetimeBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;

public class DateTimeManager extends ColumnEditView<ManageDatetimeBinding> {

    private static final String NOW = "now";

    public DateTimeManager(@NonNull Context context) {
        super(context);
    }

    public DateTimeManager(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimeManager(@NonNull Context context,
                           @Nullable FragmentManager fragmentManager) {
        super(context, ManageDatetimeBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        final var useNowAsDefault = binding.useNowAsDefault.isChecked();

        fullColumn.getColumn().getDefaultValue().setStringValue(useNowAsDefault ? NOW : null);
        fullColumn.getColumn().setDateTimeAttributes(new DateTimeAttributes());

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);
        binding.useNowAsDefault.setChecked(NOW.equals(fullColumn.getColumn().getDefaultValue().getStringValue()));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        binding.useNowAsDefault.setEnabled(enabled);
    }
}
