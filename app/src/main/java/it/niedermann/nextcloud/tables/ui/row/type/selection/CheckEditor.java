package it.niedermann.nextcloud.tables.ui.row.type.selection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.checkbox.MaterialCheckBox;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

@SuppressLint("ViewConstructor")
public class CheckEditor extends MaterialCheckBox implements ColumnEditView {

    protected final Column column;

    public CheckEditor(Context context, @NonNull Column column) {
        this(context, null, column);
    }

    public CheckEditor(Context context, @Nullable AttributeSet attrs, @NonNull Column column) {
        this(context, attrs, android.R.style.Widget_Material_CompoundButton_CheckBox, column);
    }

    public CheckEditor(Context context, @Nullable AttributeSet attrs, int defStyleAttr, @NonNull Column column) {
        super(context, attrs, defStyleAttr);
        this.column = column;
        setText(column.getTitle());
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
        return String.valueOf(isChecked());
    }
}
