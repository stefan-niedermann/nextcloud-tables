package it.niedermann.nextcloud.tables.types.editor.type.text;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.databinding.EditRichBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.editor.OnTextChangedListener;
import it.niedermann.nextcloud.tables.types.editor.type.ColumnEditView;

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
                          @Nullable Data data,
                          @NonNull DefaultValueSupplier defaultValueSupplier) throws Exception {
        super(context, null, column, data, defaultValueSupplier);
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
        final var text = binding.editText.getText();
        return text == null ? JsonNull.INSTANCE : new JsonPrimitive(text.toString());
    }

    @Override
    protected void setValue(@NonNull JsonElement value) {
        if (value.isJsonNull()) {
            binding.editText.setText(null);
        } else {
            binding.editText.setText(value.getAsString());
        }
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
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
