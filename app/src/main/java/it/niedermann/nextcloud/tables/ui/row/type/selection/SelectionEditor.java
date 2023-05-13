package it.niedermann.nextcloud.tables.ui.row.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditSelectionBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

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
                           @NonNull Data data) {
        super(context, null, column, data);
        binding = EditSelectionBinding.inflate(LayoutInflater.from(context));
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditSelectionBinding.inflate(LayoutInflater.from(context));

        setValue(data.getValue());

        final var group = new RadioGroup(context);

        for (final var selectionOption : column.getSelectionOptions()) {
            final var radio = new MaterialRadioButton(context);
            radio.setId(View.generateViewId());
            radio.setText(selectionOption.getLabel());
            radio.setChecked(Objects.equals(selectionOption.getRemoteId(), this.value));
            radio.setOnCheckedChangeListener((buttonView, isChecked) -> setValue(String.valueOf(selectionOption.getRemoteId())));
            group.addView(radio);
        }

        binding.getRoot().addView(group);

        binding.title.setText(column.getTitle());

        return binding.getRoot();
    }

    @Nullable
    @Override
    public String getValue() {
        return value == null ? null : String.valueOf(this.value);
    }

    @Override
    protected void setValue(@Nullable String value) {
        if (value == null) {
            this.value = null;
        } else {
            this.value = Long.parseLong(value);
        }
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onValueChanged();
    }
}
