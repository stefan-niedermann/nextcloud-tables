package it.niedermann.nextcloud.tables.ui.column.edit.types;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import it.niedermann.nextcloud.tables.database.model.FullColumn;

public abstract class ColumnEditView<T extends ViewBinding> extends FrameLayout {

    protected static final String KEY_COLUMN = "column";

    protected T binding;
    protected FullColumn fullColumn;
    protected FragmentManager fragmentManager;

    public ColumnEditView(@NonNull Context context) {
        super(context);
    }

    public ColumnEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColumnEditView(@NonNull Context context,
                          @NonNull T binding,
                          @Nullable FragmentManager fragmentManager
    ) {
        this(context);
        this.binding = binding;
        this.fragmentManager = fragmentManager;

        final var layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );

        setLayoutParams(layoutParams);
        addView(binding.getRoot());
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

        bundle.putSerializable(KEY_COLUMN, fullColumn);
        return bundle;
    }

    @NonNull
    public FullColumn getFullColumn() {
        return this.fullColumn;
    }

    @CallSuper
    public void setFullColumn(@NonNull FullColumn fullColumn) {
        this.fullColumn = fullColumn;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        if (state instanceof Bundle bundle) {
            if (bundle.containsKey(KEY_COLUMN)) {
                this.fullColumn = (FullColumn) bundle.getSerializable(KEY_COLUMN);
                if (this.fullColumn != null) {
                    setFullColumn(fullColumn);
                }
            }
        }
    }
}