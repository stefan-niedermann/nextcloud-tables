package it.niedermann.nextcloud.tables.features.row.editor.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.EditSelectionCheckBinding;
import it.niedermann.nextcloud.tables.features.row.editor.type.DataEditView;

public class SelectionCheckEditor extends DataEditView<EditSelectionCheckBinding> implements CompoundButton.OnCheckedChangeListener {

    public SelectionCheckEditor(@NonNull Context context) {
        super(context, EditSelectionCheckBinding.inflate(LayoutInflater.from(context)));
    }

    public SelectionCheckEditor(@NonNull Context context,
                                @Nullable AttributeSet attrs) {
        super(context, attrs, EditSelectionCheckBinding.inflate(LayoutInflater.from(context)));
    }

    public SelectionCheckEditor(@NonNull Context context,
                                @NonNull Column column) {
        super(context, EditSelectionCheckBinding.inflate(LayoutInflater.from(context)), column);

        binding.data.setText(column.getTitle());
        binding.data.setHint(column.getDescription());
        binding.data.setOnCheckedChangeListener(this);
    }

    @Override
    @Nullable
    public FullData getFullData() {
        Optional.ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .ifPresent(val -> val.setBooleanValue(binding.data.isChecked()));

        return fullData;
    }

    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getBooleanValue)
                .orElse(false);

        binding.data.setOnCheckedChangeListener(null);
        binding.getRoot().setChecked(value);
        binding.data.setOnCheckedChangeListener(this);
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onValueChanged();
    }
}
