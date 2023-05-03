package it.niedermann.nextcloud.tables.ui.row.type.text;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.EditTextviewBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.row.OnTextChangedListener;

public class TextEditor extends ColumnEditView implements OnTextChangedListener {

    protected EditTextviewBinding binding;

    public TextEditor(@NonNull Context context) {
        super(context);
        binding = EditTextviewBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot());
    }

    public TextEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditTextviewBinding.inflate(LayoutInflater.from(context));
        addView(binding.getRoot());
    }

    public TextEditor(@NonNull Context context,
                      @Nullable FragmentManager fragmentManager,
                      @NonNull Column column,
                      @Nullable Object value) {
        super(context, fragmentManager, column, value);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = EditTextviewBinding.inflate(LayoutInflater.from(context));
        binding.editText.addTextChangedListener(this);
        binding.getRoot().setHint(column.getTitle());
        binding.getRoot().setStartIconDrawable(R.drawable.baseline_short_text_24);

        if (column.getTextMaxLength() != null) {
            binding.getRoot().setCounterMaxLength(column.getTextMaxLength());
        }

        return binding.getRoot();
    }

    @Nullable
    @Override
    public Object getValue() {
        return binding.editText.getText();
    }

    @Override
    protected void setValue(@Nullable Object value) {
        if (value == null) {
            binding.editText.setText(null);
        } else {
            binding.editText.setText(String.valueOf(value));
        }
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        binding.getRoot().setError(message);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onValueChanged();
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (column.isMandatory()) {
            return TextUtils.isEmpty(binding.editText.getText())
                    ? Optional.of(getContext().getString(R.string.validation_mandatory))
                    : Optional.empty();
        }

        return super.validate();
    }
}
