package it.niedermann.nextcloud.tables.types.editor.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.databinding.EditSelectionBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;

public class SelectionEditor extends ColumnEditView implements CompoundButton.OnCheckedChangeListener {

    protected EditSelectionBinding binding;
    protected Long value;

    public SelectionEditor(Context context) {
        super(context);
    }

    public SelectionEditor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditSelectionBinding.inflate(LayoutInflater.from(context));
    }

    public SelectionEditor(@NonNull Context context,
                           @NonNull Column column,
                           @Nullable Data data,
                           @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, null, column, data, defaultValueSupplier);
        binding = EditSelectionBinding.inflate(LayoutInflater.from(context));
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditSelectionBinding.inflate(LayoutInflater.from(context));

        final var value = data.getValue();
        if (value == null) {
            throw new IllegalStateException("value must not be null");
        }
        setValue(value);

        final var group = new RadioGroup(context);

        for (final var selectionOption : column.getSelectionOptions()) {
            final var radio = new MaterialRadioButton(context);
            radio.setId(View.generateViewId());
            radio.setText(selectionOption.getLabel());
            radio.setChecked(Objects.equals(selectionOption.getRemoteId(), this.value));
            radio.setOnCheckedChangeListener((buttonView, isChecked) -> {
                final var selectionOptionRemoteId = selectionOption.getRemoteId();
                if (isChecked && selectionOptionRemoteId != null) {
                    setValue(new JsonPrimitive(selectionOptionRemoteId));
                }
            });
            group.addView(radio);
        }

        binding.getRoot().addView(group);

        binding.title.setText(column.getTitle());

        return binding.getRoot();
    }

    @NonNull
    @Override
    public JsonElement getValue() {
        return value == null ? JsonNull.INSTANCE : new JsonPrimitive(this.value);
    }

    @Override
    protected void setValue(@NonNull JsonElement value) {
        this.value = value.isJsonPrimitive()
                ? value.getAsLong()
                : null;
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onValueChanged();
    }
}
