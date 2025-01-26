package it.niedermann.nextcloud.tables.features.column.edit.types.number;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.ManageNumberStarsBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.remote.tablesV2.TablesV2API;

public class StarsManager extends ColumnEditView<ManageNumberStarsBinding> {

    private boolean enabled = false;

    public StarsManager(@NonNull Context context) {
        super(context);
        binding = ManageNumberStarsBinding.inflate(LayoutInflater.from(context));
    }

    public StarsManager(@NonNull Context context,
                        @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = ManageNumberStarsBinding.inflate(LayoutInflater.from(context));
    }

    public StarsManager(@NonNull Context context,
                        @Nullable FragmentManager fragmentManager) {
        super(context, ManageNumberStarsBinding.inflate(LayoutInflater.from(context)), fragmentManager);
        binding.stars.setStars(TablesV2API.ASSUMED_COLUMN_NUMBER_STARS_MAX_VALUE);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final var parentState = super.onSaveInstanceState();
        final var args = new Bundle();
        args.putParcelable("parent", parentState);
        args.putBoolean("enabled", enabled);
        return parentState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle args) {
            super.onRestoreInstanceState(args.getParcelable("parent"));
            enabled = args.getBoolean("enabled");
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @NonNull
    @Override
    public FullColumn getFullColumn() {
        final var fullColumn = super.getFullColumn();

        fullColumn.getColumn().getDefaultValue().setDoubleValue(Integer
                .valueOf(binding.stars.getValue())
                .doubleValue());

        return fullColumn;
    }

    @Override
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        super.setFullColumn(fullColumn);

        final var value = Optional.of(fullColumn)
                .map(FullColumn::getColumn)
                .map(Column::getDefaultValue)
                .map(Value::getDoubleValue)
                .map(Math::ceil)
                .map(Double::intValue)
                .orElse(0);

        // TODO https://github.com/nextcloud/tables/issues/1385
        fullColumn.getColumn().getDefaultValue().setDoubleValue((double) value);
        binding.stars.setResettable(!fullColumn.getColumn().isMandatory());
        binding.stars.setValue(value);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
    }
}
