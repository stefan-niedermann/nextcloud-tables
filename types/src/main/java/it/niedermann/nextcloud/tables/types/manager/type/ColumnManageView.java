package it.niedermann.nextcloud.tables.types.manager.type;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;

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
        this.fragmentManager = fragmentManager;

        final var layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );

        setLayoutParams(layoutParams);
        addView(onCreate(context));
        setColumn(column);
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

    @NonNull
    protected abstract View onCreate(@NonNull Context context);

    @NonNull
    public Column getColumn() {
        return this.column;
    }

    @CallSuper
    protected void setColumn(@NonNull Column column) {
        this.column = column;
    }
}
