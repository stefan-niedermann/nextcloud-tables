package it.niedermann.nextcloud.tables.types.editor;

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

import com.google.gson.JsonElement;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueUtil;

public abstract class ColumnEditView extends FrameLayout {

    protected static final String KEY_DATA = "data";

    protected Column column;
    protected FragmentManager fragmentManager;
    protected Data data;
    protected DefaultValueSupplier defaultValueSupplier;

    public ColumnEditView(@NonNull Context context) {
        super(context);
    }

    public ColumnEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColumnEditView(@NonNull Context context,
                          @Nullable FragmentManager fragmentManager,
                          @NonNull Column column,
                          @Nullable Data data,
                          @NonNull DefaultValueSupplier defaultValueSupplier
    ) throws Exception {
        this(context, fragmentManager, column, data, defaultValueSupplier, new DefaultValueUtil());
    }

    private ColumnEditView(@NonNull Context context,
                          @Nullable FragmentManager fragmentManager,
                          @NonNull Column column,
                          @Nullable Data data,
                          @NonNull DefaultValueSupplier defaultValueSupplier,
                          @NonNull DefaultValueUtil util
    ) {
        this(context);
        this.column = column;
        this.fragmentManager = fragmentManager;
        this.data = util.ensureDataObjectPresent(column, data, defaultValueSupplier);
        this.defaultValueSupplier = defaultValueSupplier;

        final var layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        @Px final int verticalMargin = context.getResources().getDimensionPixelSize(R.dimen.spacer_1x);
        layoutParams.setMargins(0, verticalMargin, 0, verticalMargin);
        setLayoutParams(layoutParams);
        addView(onCreate(context, this.data));
        requestLayout();
        invalidate();
    }

    @NonNull
    public Data toData() {
        data.setValue(getValue());
        return data;
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

        bundle.putSerializable(KEY_DATA, toData());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        if (state instanceof Bundle bundle) {
            if (bundle.containsKey(KEY_DATA)) {
                this.data = (Data) bundle.getSerializable(KEY_DATA);
                if (this.data != null) {
                    final var value = this.data.getValue();
                    if (value != null) {
                        setValue(value);
                    }
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
    protected abstract View onCreate(@NonNull Context context, @NonNull Data data);

    @NonNull
    protected abstract JsonElement getValue();

    protected abstract void setValue(@NonNull JsonElement value);

    public abstract void setErrorMessage(@Nullable String message);

    /**
     * @return an error message if invalid, {@link Optional#empty()} otherwise.
     */
    @NonNull
    public Optional<String> validate() {
        return Optional.empty();
    }
}
