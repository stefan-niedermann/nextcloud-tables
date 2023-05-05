package it.niedermann.nextcloud.tables.ui.row.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.databinding.EditSelectionMultiBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class MultiEditor extends ColumnEditView implements CompoundButton.OnCheckedChangeListener {

    protected EditSelectionMultiBinding binding;
    protected Map<CompoundButton, SelectionOption> checkboxes = new HashMap<>();
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
                       @Nullable Object value) {
        super(context, fragmentManager, column, value);
        binding = EditSelectionMultiBinding.inflate(LayoutInflater.from(context));
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @Nullable Object value) {
        binding = EditSelectionMultiBinding.inflate(LayoutInflater.from(context));
        checkboxes = new HashMap<>();
        selectedRemoteIds = new HashSet<>();

        for (final var selectionOption : column.getSelectionOptions()) {
            final var checkbox = new MaterialCheckBox(context);
            checkbox.setText(selectionOption.getLabel());
            binding.getRoot().addView(checkbox);
            checkboxes.put(checkbox, selectionOption);
        }

        binding.title.setText(column.getTitle());
        setValue(value);

        return binding.getRoot();
    }

    @Nullable
    @Override
    public Object getValue() {
        return serializeSelectedRemoteIds();
    }

    @Override
    protected void setValue(@Nullable Object value) {
        this.selectedRemoteIds.clear();

        if (value == null) {
            return;
        }

        this.selectedRemoteIds.addAll(deserializeSelectedRemoteIds(value));
        for (final var entry : checkboxes.entrySet()) {
            entry.getKey().setChecked(this.selectedRemoteIds.contains(entry.getValue().getRemoteId()));
        }
    }

    private Set<Long> deserializeSelectedRemoteIds(@Nullable Object value) {
        if (value instanceof String) {
            final var numbers = ((String) value)
                    .replace("[", "")
                    .replace("]", "");

            if (numbers.isBlank()) {
                return Collections.emptySet();
            }

            return Arrays.stream(numbers.split(",")).map(Double::parseDouble).map(Double::longValue).collect(Collectors.toUnmodifiableSet());
        }
        throw new IllegalArgumentException(value + " can not be parsed");
    }

    private String serializeSelectedRemoteIds() {
        return selectedRemoteIds.isEmpty()
                ? "" :
                "[" + selectedRemoteIds
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","))
                        + "]";
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final var selectionOption = checkboxes.get(buttonView);
        checkboxes.put(buttonView, selectionOption);
        onValueChanged();
    }
}
