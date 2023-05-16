package it.niedermann.nextcloud.tables.ui.row.type.text;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.EditRichBinding;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.row.OnTextChangedListener;

public class TextRichEditor extends ColumnEditView implements OnTextChangedListener {

    protected EditRichBinding binding;

    public TextRichEditor(@NonNull Context context) {
        super(context);
        binding = EditRichBinding.inflate(LayoutInflater.from(context), this, false);
        addView(binding.getRoot());
    }

    public TextRichEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = EditRichBinding.inflate(LayoutInflater.from(context), this, false);
        addView(binding.getRoot());
    }

    public TextRichEditor(@NonNull Context context,
                          @NonNull Column column,
                          @NonNull Data data) {
        super(context, null, column, data);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        binding = EditRichBinding.inflate(LayoutInflater.from(context), this, false);
        binding.editText.addTextChangedListener(this);
        binding.getRoot().setHint(column.getTitle());
        // TODO
        // if (column.getTextMaxLength() != null) {
        //      binding.getRoot().setCounterMaxLength(column.getTextMaxLength());
        // }

        setValue(data.getValue());

        return binding.getRoot();
    }

    @Nullable
    @Override
    public String getValue() {
        final var text = binding.editText.getText();
        return text == null ? null : text.toString();
    }

    @Override
    protected void setValue(@Nullable String value) {
        binding.editText.setText(value);
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
