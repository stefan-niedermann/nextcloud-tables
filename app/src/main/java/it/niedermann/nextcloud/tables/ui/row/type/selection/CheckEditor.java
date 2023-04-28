package it.niedermann.nextcloud.tables.ui.row.type.selection;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.checkbox.MaterialCheckBox;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class CheckEditor extends ColumnEditView implements CompoundButton.OnCheckedChangeListener {

    protected CheckBox checkBox;

    public CheckEditor(Context context) {
        super(context);
    }

    public CheckEditor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        checkBox = new MaterialCheckBox(context);
        checkBox.setText(column.getTitle());
        checkBox.setHint(column.getDescription());
        checkBox.setChecked(Boolean.parseBoolean(column.getSelectionDefault()));
        checkBox.setOnCheckedChangeListener(this);
        return checkBox;
    }

    @Nullable
    @Override
    public Object getValue() {
        return String.valueOf(checkBox.isChecked());
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        checkBox.setError(message);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        onValueChanged();
    }
}
