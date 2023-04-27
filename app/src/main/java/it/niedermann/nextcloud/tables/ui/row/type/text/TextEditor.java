package it.niedermann.nextcloud.tables.ui.row.type.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.R;
import com.google.android.material.textfield.TextInputLayout;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.row.OnTextChangedListener;

@SuppressLint("ViewConstructor")
public class TextEditor extends TextInputLayout implements ColumnEditView, OnTextChangedListener {

    protected final Column column;
    protected final EditText editText;

    public TextEditor(@NonNull Context context, @NonNull Column column) {
        this(context, null, column);
    }

    protected TextEditor(@NonNull Context context, @Nullable AttributeSet attrs, @NonNull Column column) {
        this(context, attrs, R.attr.textInputStyle, column);
    }

    protected TextEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, @NonNull Column column) {
        super(context, attrs, defStyleAttr);
        this.column = column;
        editText = new EditText(context);
        editText.addTextChangedListener(this);
        final var editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final var textInputLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        setLayoutParams(textInputLayoutParams);
        addView(editText, editTextParams);
        setHint(column.getTitle());
        if (column.getTextMaxLength() != null) {
            setCounterMaxLength(column.getTextMaxLength());
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public Column getColumn() {
        return column;
    }

    @Nullable
    @Override
    public Object getValue() {
        return editText.getText();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
