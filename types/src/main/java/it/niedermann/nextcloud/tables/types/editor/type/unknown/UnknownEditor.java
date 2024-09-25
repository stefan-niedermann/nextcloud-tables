package it.niedermann.nextcloud.tables.types.editor.type.unknown;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.gson.JsonElement;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.databinding.EditTextviewBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.ColumnEditView;

public class UnknownEditor extends ColumnEditView {

    protected EditTextviewBinding binding;
    private JsonElement value;

    public UnknownEditor(@NonNull Context context) {
        super(context);
        binding = EditTextviewBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot());
    }

    public UnknownEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditTextviewBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot());
    }

    public UnknownEditor(@NonNull Context context,
                         @Nullable FragmentManager fragmentManager,
                         @NonNull Column column,
                         @Nullable Data data,
                         @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, fragmentManager, column, data, defaultValueSupplier);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditTextviewBinding.inflate(LayoutInflater.from(context));
        binding.getRoot().setHint(column.getTitle());
        binding.getRoot().setStartIconDrawable(R.drawable.baseline_question_mark_24);
        binding.getRoot().setEnabled(false);
        binding.getRoot().setHelperText(context.getString(R.string.unsupported_column_type, column.getType() + "/" + column.getSubtype()));

        final var value = data.getValue();
        if (value == null) {
            throw new IllegalStateException("value must not be null");
        }
        setValue(value);

        return binding.getRoot();
    }

    @NonNull
    @Override
    public JsonElement getValue() {
        return this.value;
    }

    @Override
    protected void setValue(@NonNull JsonElement value) {
        this.value = value;
        binding.editText.setText(value.toString());
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }
}
