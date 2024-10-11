package it.niedermann.nextcloud.tables.ui.row.edit.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.EditSelectionMultiBinding;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;

public class SelectionMultiEditor extends DataEditView<EditSelectionMultiBinding> {

    protected final List<SelectionOption> selected = new ArrayList<>();

    public SelectionMultiEditor(@NonNull Context context) {
        super(context, EditSelectionMultiBinding.inflate(LayoutInflater.from(context)));
    }

    public SelectionMultiEditor(@NonNull Context context,
                                @Nullable AttributeSet attrs) {
        super(context, attrs, EditSelectionMultiBinding.inflate(LayoutInflater.from(context)));
    }

    public SelectionMultiEditor(@NonNull Context context,
                                @NonNull FullColumn fullColumn) {
        super(context, EditSelectionMultiBinding.inflate(LayoutInflater.from(context)), fullColumn.getColumn());

        for (final var selectionOption : fullColumn.getSelectionOptions()) {
            final var checkbox = new MaterialCheckBox(context);
            checkbox.setText(selectionOption.getLabel());
            checkbox.setId(View.generateViewId());
            checkbox.setChecked(selected.contains(selectionOption));
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selected.add(selectionOption);
                } else {
                    selected.remove(selectionOption);
                }
            });
            binding.getRoot().addView(checkbox);
        }

        binding.title.setText(fullColumn.getColumn().getTitle());
    }

    @Override
    public @Nullable FullData getFullData() {
        Optional.ofNullable(fullData)
                .ifPresent(val -> val.setSelectionOptions(selected));

        return fullData;
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        selected.clear();
        Optional.ofNullable(fullData.getSelectionOptions())
                .ifPresent(selected::addAll);
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
    }
}
