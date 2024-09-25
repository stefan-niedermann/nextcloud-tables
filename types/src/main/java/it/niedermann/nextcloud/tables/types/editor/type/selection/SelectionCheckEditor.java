package it.niedermann.nextcloud.tables.types.editor.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.databinding.EditSelectionCheckBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.ColumnEditView;

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
                                @Nullable Data data,
                                @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, null, column, data, defaultValueSupplier);
        binding = EditSelectionCheckBinding.inflate(LayoutInflater.from(context));
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditSelectionCheckBinding.inflate(LayoutInflater.from(context));
        binding.getRoot().setText(column.getTitle());
        binding.getRoot().setHint(column.getDescription());
        final var value = data.getValue();
        if (value == null) {
            throw new IllegalStateException("value must not be null");
        }
        setValue(value);
        binding.getRoot().setOnCheckedChangeListener(this);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public JsonElement getValue() {
        return new JsonPrimitive(binding.getRoot().isChecked());
    }

    @Override
    protected void setValue(@NonNull JsonElement value) {
        binding.getRoot().setChecked(!value.isJsonNull() && value.getAsBoolean());
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setValue(new JsonPrimitive(isChecked));
        onValueChanged();
    }
}
