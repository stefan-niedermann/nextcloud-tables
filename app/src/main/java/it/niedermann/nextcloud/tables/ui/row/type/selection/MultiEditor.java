package it.niedermann.nextcloud.tables.ui.row.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditSelectionMultiBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class MultiEditor extends ColumnEditView {

    protected EditSelectionMultiBinding binding;
    protected Set<Long> selectedRemoteIds = new HashSet<>();

    public MultiEditor(Context context) {
        super(context);
    }

    public MultiEditor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditSelectionMultiBinding.inflate(LayoutInflater.from(context));
    }

    public MultiEditor(@NonNull Context context,
                       @Nullable FragmentManager fragmentManager,
                       @NonNull Column column,
                       @NonNull Data data) {
        super(context, fragmentManager, column, data);
        binding = EditSelectionMultiBinding.inflate(LayoutInflater.from(context));
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditSelectionMultiBinding.inflate(LayoutInflater.from(context));
        selectedRemoteIds = new HashSet<>();

        setValue(data.getValue());

        for (final var selectionOption : column.getSelectionOptions()) {
            final var checkbox = new MaterialCheckBox(context);
            checkbox.setText(selectionOption.getLabel());
            checkbox.setChecked(selectedRemoteIds.contains(selectionOption.getRemoteId()));
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedRemoteIds.add(selectionOption.getRemoteId());
                } else {
                    selectedRemoteIds.remove(selectionOption.getRemoteId());
                }
            });
            binding.getRoot().addView(checkbox);
        }

        binding.title.setText(column.getTitle());

        return binding.getRoot();
    }

    @Nullable
    @Override
    public String getValue() {
        return selectedRemoteIds.isEmpty() ? "" :
                selectedRemoteIds
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
    }

    @Override
    protected void setValue(@Nullable String value) {
        this.selectedRemoteIds.clear();

        if (value == null || value.isBlank()) {
            return;
        }

        final var optionIDs = Arrays.stream(value.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toUnmodifiableSet());
        this.selectedRemoteIds.addAll(optionIDs);
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
    }
}
