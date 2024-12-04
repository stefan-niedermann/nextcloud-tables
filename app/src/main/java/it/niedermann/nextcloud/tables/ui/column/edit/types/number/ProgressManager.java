package it.niedermann.nextcloud.tables.ui.column.edit.types.number;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.ManageNumberProgressBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;

public class ProgressManager extends ColumnEditView<ManageNumberProgressBinding> {

    public ProgressManager(@NonNull Context context) {
        super(context);
    }

    public ProgressManager(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressManager(@NonNull Context context,
                           @Nullable FragmentManager fragmentManager) {
        super(context, ManageNumberProgressBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        fullColumn.getColumn().getDefaultValue().setDoubleValue((double) binding.progress.getValue());

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        // https://github.com/nextcloud/tables/issues/1385
        Optional.ofNullable(fullColumn.getColumn())
                .map(Column::getDefaultValue)
                .map(Value::getDoubleValue)
                .map(Double::floatValue)
                .ifPresent(binding.progress::setValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        binding.progress.setEnabled(enabled);
    }
}
