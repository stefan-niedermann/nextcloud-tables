package it.niedermann.nextcloud.tables.ui.row.edit.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.EditSelectionBinding;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;

public class SelectionEditor extends DataEditView<EditSelectionBinding> implements CompoundButton.OnCheckedChangeListener {

    @Nullable
    protected SelectionOption selected;

    public SelectionEditor(@NonNull Context context) {
        super(context, EditSelectionBinding.inflate(LayoutInflater.from(context)));
    }

    public SelectionEditor(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs, EditSelectionBinding.inflate(LayoutInflater.from(context)));
    }

    public SelectionEditor(@NonNull Context context,
                           @NonNull FullColumn fullColumn) {
        super(context, EditSelectionBinding.inflate(LayoutInflater.from(context)), fullColumn.getColumn());

        final var group = new RadioGroup(context);
        for (final var selectionOption : fullColumn.getSelectionOptions()) {
            final var radio = new MaterialRadioButton(context);
            radio.setId(View.generateViewId());
            radio.setText(selectionOption.getLabel());
            radio.setChecked(Objects.equals(selectionOption, this.selected));
            radio.setOnCheckedChangeListener((buttonView, isChecked) -> selected = selectionOption);
            group.addView(radio);
        }

        binding.getRoot().addView(group);
        binding.title.setText(column.getTitle());
    }

    @Override
    @Nullable
    public FullData getFullData() {
        final var value = Optional
                .ofNullable(selected)
                .map(List::of)
                .orElseGet(Collections::emptyList);

        Optional.ofNullable(fullData)
                .ifPresent(val -> val.setSelectionOptions(value));

        return fullData;
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        this.selected = Optional
                .ofNullable(fullData.getSelectionOptions())
                .map(List::stream)
                .flatMap(Stream::findAny)
                .orElse(null);
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onValueChanged();
    }
}
