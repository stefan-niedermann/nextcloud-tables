package it.niedermann.nextcloud.tables.ui.row.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditSelectionCheckBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class SelectionCheckEditor extends ColumnEditView implements CompoundButton.OnCheckedChangeListener {

    protected EditSelectionCheckBinding binding;

    public SelectionCheckEditor(Context context) {
        super(context);
    }

    public SelectionCheckEditor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditSelectionCheckBinding.inflate(LayoutInflater.from(context));
    }

    public SelectionCheckEditor(@NonNull Context context,
                                @NonNull Column column,
                                @NonNull Data data) {
        super(context, null, column, data);
        binding = EditSelectionCheckBinding.inflate(LayoutInflater.from(context));
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditSelectionCheckBinding.inflate(LayoutInflater.from(context));
        binding.getRoot().setText(column.getTitle());
        binding.getRoot().setHint(column.getDescription());
        setValue(data.getValue());
        binding.getRoot().setOnCheckedChangeListener(this);
        return binding.getRoot();
    }

    @Nullable
    @Override
    public String getValue() {
        return String.valueOf(binding.getRoot().isChecked());
    }

    @Override
    protected void setValue(@Nullable String value) {
        binding.getRoot().setChecked(Boolean.parseBoolean(value));
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setValue(String.valueOf(isChecked));
        onValueChanged();
    }
}
