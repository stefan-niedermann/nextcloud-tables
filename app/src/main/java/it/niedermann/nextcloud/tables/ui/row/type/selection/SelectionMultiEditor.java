package it.niedermann.nextcloud.tables.ui.row.type.selection;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditSelectionMultiBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class SelectionMultiEditor extends ColumnEditView {

    protected EditSelectionMultiBinding binding;
    protected Set<Long> selectedRemoteIds;

    public SelectionMultiEditor(Context context) {
        super(context);
    }

    public SelectionMultiEditor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditSelectionMultiBinding.inflate(LayoutInflater.from(context));
    }

    public SelectionMultiEditor(@NonNull Context context,
                                @NonNull Column column,
                                @NonNull Data data) {
        super(context, null, column, data);
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
            checkbox.setId(View.generateViewId());
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
        return selectedRemoteIds.isEmpty() ? null :
                selectedRemoteIds
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
    }

    @Override
    protected void setValue(@Nullable String value) {
        selectedRemoteIds.clear();

        if (TextUtils.isEmpty(value)) {
            return;
        }

        final var optionIDs = Arrays.stream(value.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toUnmodifiableSet());
        selectedRemoteIds.addAll(optionIDs);
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
    }
}
