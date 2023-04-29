package it.niedermann.nextcloud.tables.ui.row.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.EditSelectionCheckBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class CheckEditor extends ColumnEditView implements CompoundButton.OnCheckedChangeListener {

    protected EditSelectionCheckBinding binding;

    public CheckEditor(Context context) {
        super(context);
    }

    public CheckEditor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditSelectionCheckBinding.inflate(LayoutInflater.from(context));
    }

    public CheckEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
        binding = EditSelectionCheckBinding.inflate(LayoutInflater.from(context));
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = EditSelectionCheckBinding.inflate(LayoutInflater.from(context));
        binding.getRoot().setText(column.getTitle());
        binding.getRoot().setHint(column.getDescription());
        binding.getRoot().setChecked(Boolean.parseBoolean(column.getSelectionDefault()));
        binding.getRoot().setOnCheckedChangeListener(this);
        return binding.getRoot();
    }

    @Nullable
    @Override
    public Object getValue() {
        return String.valueOf(binding.getRoot().isChecked());
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onValueChanged();
    }
}
