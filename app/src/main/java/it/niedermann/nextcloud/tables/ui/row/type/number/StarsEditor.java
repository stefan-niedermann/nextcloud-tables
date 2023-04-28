package it.niedermann.nextcloud.tables.ui.row.type.number;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.ui.row.ColumnEditView;

public class StarsEditor extends ColumnEditView {

    @IntRange(from = 0, to = 5)
    protected int value = 0;
    protected LinearLayout linearLayout;
    protected Drawable starFilled;
    protected Drawable starBorder;

    public StarsEditor(@NonNull Context context) {
        super(context);
    }

    public StarsEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StarsEditor(@NonNull Context context, @NonNull Column column) {
        super(context, column);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        starFilled = ContextCompat.getDrawable(context, R.drawable.baseline_star_24);
        starBorder = ContextCompat.getDrawable(context, R.drawable.baseline_star_border_24);

        for (int i = 0; i < 5; i++) {
            final var value = i;
            final var imageButton = new ImageButton(context);
            imageButton.setBackground(null);
            imageButton.setOnClickListener(v -> setValue(value));
            linearLayout.addView(imageButton);
        }

        final var defaultValue = column.getNumberDefault();
        setValue(defaultValue == null ? 0 : defaultValue.intValue());

        return linearLayout;
    }

    private void setValue(@IntRange(from = 0, to = 5) int value) {
        this.value = value;
        for (int i = 0; i < 5; i++) {
            final var imageButton = (ImageButton) linearLayout.getChildAt(i);
            imageButton.setImageDrawable(i <= value ? starFilled : starBorder);
        }
    }

    @Nullable
    @Override
    protected Object getValue() {
        return value;
    }

    @Override
    protected void setErrorMessage(@Nullable String message) {
        // TODO
    }
}
