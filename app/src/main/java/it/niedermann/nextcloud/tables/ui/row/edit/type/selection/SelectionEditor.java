package it.niedermann.nextcloud.tables.ui.row.edit.type.selection;

import static java.util.Collections.emptyMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.EditSelectionBinding;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;

public class SelectionEditor extends DataEditView<EditSelectionBinding> {

    @Nullable
    protected SelectionOption selected;
    @NonNull
    private final Map<Long, RadioButton> selectionOptionIdAndRadioButtons;

    public SelectionEditor(@NonNull Context context) {
        super(context, EditSelectionBinding.inflate(LayoutInflater.from(context)));
        selectionOptionIdAndRadioButtons = emptyMap();
    }

    public SelectionEditor(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        super(context, attrs, EditSelectionBinding.inflate(LayoutInflater.from(context)));
        selectionOptionIdAndRadioButtons = emptyMap();
    }

    public SelectionEditor(@NonNull Context context,
                           @NonNull FullColumn fullColumn) {
        super(context, EditSelectionBinding.inflate(LayoutInflater.from(context)), fullColumn.getColumn());

        selectionOptionIdAndRadioButtons = new HashMap<>(fullColumn.getSelectionOptions().size());

        final var group = new RadioGroup(context);

        for (final var selectionOption : fullColumn.getSelectionOptions()) {

            final var radio = new MaterialRadioButton(context);

            radio.setId(View.generateViewId());
            radio.setText(selectionOption.getLabel());
            radio.setOnCheckedChangeListener((buttonView, isChecked) -> {
                selected = selectionOption;
                onValueChanged();
            });

            selectionOptionIdAndRadioButtons.put(selectionOption.getId(), radio);
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

        Optional.ofNullable(this.selected)
                .map(SelectionOption::getId)
                .map(selectionOptionIdAndRadioButtons::get)
                .ifPresent(radio -> radio.setChecked(false));

        this.selected = Optional.of(fullData.getSelectionOptions())
                .map(List::stream)
                .flatMap(Stream::findAny)
                .orElse(null);

        Optional.ofNullable(this.selected)
                .map(SelectionOption::getId)
                .map(selectionOptionIdAndRadioButtons::get)
                .ifPresent(radio -> radio.setSelected(true));
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
    }
}
