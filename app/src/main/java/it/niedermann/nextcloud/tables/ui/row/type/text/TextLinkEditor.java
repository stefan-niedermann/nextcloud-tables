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
import it.niedermann.nextcloud.tables.database.entity.Data;

public class TextLinkEditor extends TextEditor {

    public TextLinkEditor(@NonNull Context context) {
        super(context);
    }

    public TextLinkEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextLinkEditor(@NonNull Context context,
                          @NonNull Column column,
                          @NonNull Data data) {
        super(context, null, column, data);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context, @NonNull Data data) {
        final var view = super.onCreate(context, data);
        binding.editText.setMaxLines(1);
        binding.editText.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        binding.getRoot().setStartIconDrawable(R.drawable.baseline_link_24);
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
