package it.niedermann.nextcloud.tables.ui.row;

import android.text.Editable;
import android.text.TextWatcher;

public interface OnTextChangedListener extends TextWatcher {

    @Override
    default void afterTextChanged(Editable s) {
    }

    @Override
    default void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
}