package it.niedermann.nextcloud.tables.types.editor.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.databinding.EditSelectionMultiBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;

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
                                @Nullable Data data,
                                @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, null, column, data, defaultValueSupplier);
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

    @NonNull
    @Override
    public JsonElement getValue() {
        final var ids = new JsonArray();
        for (final var id : selectedRemoteIds) {
            ids.add(id);
        }
        return ids;
    }

    @Override
    protected void setValue(@NonNull JsonElement value) {
        selectedRemoteIds.clear();

        if (value.isJsonArray()) {
            final var optionIDs = value
                    .getAsJsonArray()
                    .asList()
                    .stream()
                    .filter(JsonElement::isJsonPrimitive)
                    .map(JsonElement::getAsLong)
                    .collect(Collectors.toUnmodifiableSet());
            selectedRemoteIds.addAll(optionIDs);
        }
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
    }
}
