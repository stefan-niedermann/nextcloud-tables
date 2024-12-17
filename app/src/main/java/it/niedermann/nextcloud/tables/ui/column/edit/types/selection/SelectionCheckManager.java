package it.niedermann.nextcloud.tables.ui.column.edit.types.selection;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.attributes.SelectionAttributes;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ManageSelectionCheckBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;

public class SelectionCheckManager extends ColumnEditView<ManageSelectionCheckBinding> {

    public SelectionCheckManager(@NonNull Context context) {
        super(context);
    }

    public SelectionCheckManager(@NonNull Context context,
                                 @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectionCheckManager(@NonNull Context context,
                                 @Nullable FragmentManager fragmentManager) {
        super(context, ManageSelectionCheckBinding.inflate(LayoutInflater.from(context)), fragmentManager);
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        fullColumn.getColumn().getDefaultValue().setBooleanValue(binding.defaultValue.isChecked());
        fullColumn.getColumn().setSelectionAttributes(new SelectionAttributes());

        return super.getFullColumn();
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        binding.defaultValue.setChecked(Boolean.TRUE.equals(fullColumn.getColumn().getDefaultValue().getBooleanValue()));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        binding.defaultValue.setEnabled(enabled);
    }
}
