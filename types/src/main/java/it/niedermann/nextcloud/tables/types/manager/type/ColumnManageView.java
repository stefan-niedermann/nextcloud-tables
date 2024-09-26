package it.niedermann.nextcloud.tables.types.manager.type;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.R;

public abstract class ColumnManageView extends FrameLayout {

    protected static final String KEY_COLUMN = "column";

    protected Column column;
    protected FragmentManager fragmentManager;

    public ColumnManageView(@NonNull Context context) {
        super(context);
    }

    public ColumnManageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColumnManageView(@NonNull Context context,
                             @NonNull Column column,
                             @Nullable FragmentManager fragmentManager
    ) {
        this(context);
        this.column = column;
        this.fragmentManager = fragmentManager;

        final var layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );

        @Px final int verticalMargin = context.getResources().getDimensionPixelSize(R.dimen.spacer_1x);
        layoutParams.setMargins(0, verticalMargin, 0, verticalMargin);
        setLayoutParams(layoutParams);
        addView(onCreate(context));
        requestLayout();
        invalidate();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final var state = super.onSaveInstanceState();
        final Bundle bundle;

        if (state instanceof Bundle) {
            bundle = (Bundle) state;
        } else if (state == null) {
            bundle = new Bundle();
        } else {
            throw new IllegalStateException("Expected super state being null or " + Bundle.class.getSimpleName() + " but was " + state.getClass().getSimpleName());
        }

        bundle.putSerializable(KEY_COLUMN, column);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        if (state instanceof Bundle bundle) {
            if (bundle.containsKey(KEY_COLUMN)) {
                this.column = (Column) bundle.getSerializable(KEY_COLUMN);
                if (this.column != null) {
                    setColumn(column);
                }
            }
        }
    }

    protected void onValueChanged() {
        validate().ifPresentOrElse(
                this::setErrorMessage,
                () -> setErrorMessage(null)
        );
    }

    @NonNull
    protected abstract View onCreate(@NonNull Context context);

    @NonNull
    public abstract Column getColumn();

    protected abstract void setColumn(@NonNull Column column);

    public abstract void setErrorMessage(@Nullable String message);

    /**
     * @return an error message if invalid, {@link Optional#empty()} otherwise.
     */
    @NonNull
    public Optional<String> validate() {
        return Optional.empty();
    }
}
