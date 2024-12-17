package it.niedermann.nextcloud.tables.ui.row.edit.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.EditSelectionMultiBinding;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;

public class SelectionMultiEditor extends DataEditView<EditSelectionMultiBinding> {

    protected final List<SelectionOption> checkedSelectionOptions = new ArrayList<>();
    @NonNull
    private final ConcurrentMap<Long, CheckBox> selectionOptionIdAndCheckBoxes;

    public SelectionMultiEditor(@NonNull Context context) {
        super(context, EditSelectionMultiBinding.inflate(LayoutInflater.from(context)));
        selectionOptionIdAndCheckBoxes = new ConcurrentHashMap<>(0);
    }

    public SelectionMultiEditor(@NonNull Context context,
                                @Nullable AttributeSet attrs) {
        super(context, attrs, EditSelectionMultiBinding.inflate(LayoutInflater.from(context)));
        selectionOptionIdAndCheckBoxes = new ConcurrentHashMap<>(0);
    }

    public SelectionMultiEditor(@NonNull Context context,
                                @NonNull FullColumn fullColumn) {
        super(context, EditSelectionMultiBinding.inflate(LayoutInflater.from(context)), fullColumn.getColumn());

        selectionOptionIdAndCheckBoxes = new ConcurrentHashMap<>(fullColumn.getSelectionOptions().size());

        for (final var selectionOption : fullColumn.getSelectionOptions()) {
            final var checkbox = new MaterialCheckBox(context);
            checkbox.setText(selectionOption.getLabel());
            checkbox.setId(View.generateViewId());
            checkbox.setChecked(checkedSelectionOptions.contains(selectionOption));
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkedSelectionOptions.add(selectionOption);
                } else {
                    checkedSelectionOptions.remove(selectionOption);
                }
            });
            selectionOptionIdAndCheckBoxes.put(selectionOption.getId(), checkbox);
            binding.getRoot().addView(checkbox);
        }

        binding.title.setText(fullColumn.getColumn().getTitle());
    }

    @Override
    public @Nullable FullData getFullData() {
        Optional.ofNullable(fullData)
                .ifPresent(val -> val.setSelectionOptions(checkedSelectionOptions));

        return fullData;
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        checkedSelectionOptions.clear();
        Optional.of(fullData.getSelectionOptions())
                .ifPresent(checkedSelectionOptions::addAll);

        //noinspection DataFlowIssue
        this.checkedSelectionOptions
                .stream()
                .map(SelectionOption::getId)
                .map(selectionOptionIdAndCheckBoxes::get)
                .filter(Objects::nonNull)
                .map(checkbox -> (Runnable) () -> checkbox.setChecked(true))
                .forEach(this::post);
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
    }
}
