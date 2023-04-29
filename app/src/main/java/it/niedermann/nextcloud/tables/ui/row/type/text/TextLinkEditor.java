package it.niedermann.nextcloud.tables.ui.row.type.text;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;

public class TextLinkEditor extends TextEditor {

    public TextLinkEditor(@NonNull Context context) {
        super(context);
    }

    public TextLinkEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextLinkEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        final var view = super.onCreate(context);
        binding.editText.setMaxLines(1);
        binding.editText.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        return view;
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        if (TextUtils.isEmpty(binding.editText.getText())) {
            if (column.isMandatory()) {
                return Optional.of(getContext().getString(R.string.validation_mandatory));
            } else {
                return Optional.empty();
            }
        } else {
            return Patterns.WEB_URL.matcher(binding.editText.getText()).matches()
                    ? Optional.empty()
                    : Optional.of(getContext().getString(R.string.validation_text_link));
        }
    }
}
