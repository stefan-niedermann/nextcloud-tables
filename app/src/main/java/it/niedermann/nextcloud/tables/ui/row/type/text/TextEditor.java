package it.niedermann.nextcloud.tables.ui.row.type.text;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.row.OnTextChangedListener;

public class TextEditor extends ColumnEditView implements OnTextChangedListener {

    protected TextInputLayout textInputLayout;
    protected EditText editText;

    public TextEditor(@NonNull Context context) {
        super(context);
    }

    public TextEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        textInputLayout = new TextInputLayout(context);
        editText = new EditText(context);
        editText.addTextChangedListener(this);
        final var editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final var textInputLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        textInputLayout.setLayoutParams(textInputLayoutParams);
        textInputLayout.addView(editText, editTextParams);
        textInputLayout.setHint(column.getTitle());
        if (column.getTextMaxLength() != null) {
            textInputLayout.setCounterMaxLength(column.getTextMaxLength());
        }

        return textInputLayout;
    }

    @Nullable
    @Override
    public Object getValue() {
        return editText.getText();
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        textInputLayout.setError(message);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onValueChanged();
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (column.isMandatory()) {
            return TextUtils.isEmpty(editText.getText())
                    ? Optional.of(getContext().getString(R.string.validation_mandatory))
                    : Optional.empty();
        }
        
        return super.validate();
    }
}
