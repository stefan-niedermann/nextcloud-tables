package it.niedermann.nextcloud.tables.ui.row.type;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditTextviewBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class UnknownEditor extends ColumnEditView {

    protected EditTextviewBinding binding;
    private String value;

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
                         @NonNull Data data) {
        super(context, fragmentManager, column, data);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditTextviewBinding.inflate(LayoutInflater.from(context));
        binding.getRoot().setHint(column.getTitle());
        binding.getRoot().setStartIconDrawable(R.drawable.baseline_question_mark_24);
        binding.getRoot().setEnabled(false);
        binding.getRoot().setHelperText(context.getString(R.string.unsupported_column_type, column.getType() + "/" + column.getSubtype()));

        setValue(data.getValue());

        return binding.getRoot();
    }

    @Nullable
    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    protected void setValue(@Nullable String value) {
        this.value = value;
        binding.editText.setText(value);
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }
}
