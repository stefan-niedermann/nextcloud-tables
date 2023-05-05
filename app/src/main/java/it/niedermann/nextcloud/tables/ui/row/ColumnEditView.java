package it.niedermann.nextcloud.tables.ui.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;

import java.util.Optional;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;

public abstract class ColumnEditView extends FrameLayout {

    protected Column column;
    protected FragmentManager fragmentManager;
    protected Data data;

    public ColumnEditView(@NonNull Context context) {
        super(context);
    }

    public ColumnEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColumnEditView(@NonNull Context context,
                          @Nullable FragmentManager fragmentManager,
                          @NonNull Column column,
                          @NonNull Data data) {
        this(context);
        this.column = column;
        this.fragmentManager = fragmentManager;
        this.data = data;

        final var layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, DimensionUtil.INSTANCE.dpToPx(context, R.dimen.spacer_1x), 0, DimensionUtil.INSTANCE.dpToPx(context, R.dimen.spacer_1x));
        setLayoutParams(layoutParams);


        try {
            addView(onCreate(context, data));

            requestLayout();
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            showExceptionView(context, e);
        }
    }

    @NonNull
    public Data toData() {
        data.setValue(getValue());
        return data;
    }

    protected void onValueChanged() {
        validate().ifPresentOrElse(
                this::setErrorMessage,
                () -> setErrorMessage(null)
        );
    }

    @NonNull
    protected abstract View onCreate(@NonNull Context context, @NonNull Data data);

    @Nullable
    protected abstract Object getValue();

    protected abstract void setValue(@Nullable Object value);

    protected abstract void setErrorMessage(@Nullable String message);

    /**
     * @return an error message if invalid, {@link Optional#empty()} otherwise.
     */
    @NonNull
    public Optional<String> validate() {
        return Optional.empty();
    }

    private void showExceptionView(@NonNull Context context, @NonNull Exception e) {
        removeAllViews();
        final var layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        final var errorMessage = new TextView(context);
        errorMessage.setText(column.getTitle() + " could not be displayed.");
        layout.addView(errorMessage);
        if (fragmentManager != null) {
            final var btn = new MaterialButton(context);
            btn.setText(R.string.simple_exception);
            btn.setOnClickListener(v -> ExceptionDialogFragment.newInstance(e, null).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName()));
            layout.addView(btn);
        }
        addView(layout);
    }

    @FunctionalInterface
    interface Factory {
        ColumnEditView create(@NonNull Context context, @Nullable FragmentManager fragmentManager, @NonNull Column column, @NonNull Data data);
    }
}
