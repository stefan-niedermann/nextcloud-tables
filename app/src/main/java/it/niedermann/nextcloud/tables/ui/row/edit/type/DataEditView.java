package it.niedermann.nextcloud.tables.ui.row.edit.type;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullData;

public abstract class DataEditView<T extends ViewBinding> extends FrameLayout {

    protected static final String KEY_DATA = "data";

    protected final T binding;
    protected final Column column;
    protected final FragmentManager fragmentManager;

    @Nullable
    protected FullData fullData;

    public DataEditView(@NonNull Context context, @NonNull T binding) {
        super(context);
        this.binding = binding;
        this.column = null;
        this.fragmentManager = null;
        addView(binding.getRoot());
    }

    public DataEditView(@NonNull Context context,
                        @Nullable AttributeSet attrs,
                        @NonNull T binding) {
        super(context, attrs);
        this.binding = null;
        this.column = null;
        this.fragmentManager = null;
        addView(binding.getRoot());
    }

    protected DataEditView(@NonNull Context context,
                           @NonNull T binding,
                           @NonNull Column column
    ) {
        this(context, binding, column, null);
    }

    protected DataEditView(@NonNull Context context,
                           @NonNull T binding,
                           @NonNull Column column,
                           @Nullable FragmentManager fragmentManager
    ) {
        super(context);
        this.binding = binding;
        this.column = column;
        this.fragmentManager = fragmentManager;

        final var layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        @Px final int verticalMargin = context.getResources().getDimensionPixelSize(R.dimen.spacer_1x);
        layoutParams.setMargins(0, verticalMargin, 0, verticalMargin);
        setLayoutParams(layoutParams);
        addView(binding.getRoot());
        requestLayout();
        invalidate();
    }

    @Nullable
    public FullData getFullData() {
        return fullData;
    }

    @CallSuper
    public void setFullData(@NonNull FullData fullData) {
        this.fullData = fullData;
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

        bundle.putSerializable(KEY_DATA, getFullData());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        if (state instanceof Bundle bundle) {
            if (bundle.containsKey(KEY_DATA)) {
                this.fullData = (FullData) bundle.getSerializable(KEY_DATA);
                if (this.fullData != null) {
                    setFullData(fullData);
                }
            }
        }
    }

    protected void onValueChanged() {
        validate().ifPresentOrElse(
                this::setErrorMessage,
                () -> setErrorMessage(null));
    }

    public abstract void setErrorMessage(@Nullable String message);

    /**
     * @return an error message if invalid, {@link Optional#empty()} otherwise.
     */
    @NonNull
    public Optional<String> validate() {
        return Optional.empty();
    }
}
