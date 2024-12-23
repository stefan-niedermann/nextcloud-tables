package it.niedermann.nextcloud.tables.features.row.edit.type.text;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.EditRichBinding;
import it.niedermann.nextcloud.tables.features.row.edit.OnTextChangedListener;
import it.niedermann.nextcloud.tables.features.row.edit.type.DataEditView;

public class TextRichEditor extends DataEditView<EditRichBinding> implements OnTextChangedListener {

    public TextRichEditor(@NonNull Context context) {
        super(context, EditRichBinding.inflate(LayoutInflater.from(context)));
    }

    public TextRichEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, EditRichBinding.inflate(LayoutInflater.from(context)));
    }

    public TextRichEditor(@NonNull Context context,
                          @NonNull Column column) {
        super(context, EditRichBinding.inflate(LayoutInflater.from(context)), column);

        binding.editText.addTextChangedListener(this);
        binding.getRoot().setHint(column.getTitle());

        final var maxLength = Optional
                .of(column.getTextAttributes())
                .map(TextAttributes::textMaxLength);

        if (maxLength.isPresent()) {
            binding.getRoot().setCounterMaxLength(maxLength.get());
            binding.getRoot().setCounterEnabled(true);
        } else {
            binding.getRoot().setCounterEnabled(false);
        }
    }

    @Override
    @Nullable
    public FullData getFullData() {
        final var value = Optional
                .ofNullable(binding.editText.getText())
                .map(Editable::toString)
                .orElse(null);

        Optional.ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .ifPresent(val -> val.setStringValue(value));

        return fullData;
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        final var value = Optional
                .of(fullData.getData())
                .map(Data::getValue)
                .map(Value::getStringValue)
                .orElse(null);

        binding.editText.setText(value);
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
