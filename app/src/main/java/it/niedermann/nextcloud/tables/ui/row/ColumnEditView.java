package it.niedermann.nextcloud.tables.ui.row;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;

public abstract class ColumnEditView extends FrameLayout {

    protected Column column;

    public ColumnEditView(@NonNull Context context) {
        super(context);
    }

    public ColumnEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColumnEditView(@NonNull Context context, @NonNull Column column) {
        this(context);
        this.column = column;

        final var params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        setLayoutParams(params);
        addView(onCreate(context));
        invalidate();
    }

    @NonNull
    public Data toData() {
        Objects.requireNonNull(column, "This is only available after setting the column.");
        final var data = new Data();
        data.setAccountId(column.getAccountId());
        data.setColumnId(column.getId());
        data.setRemoteColumnId(column.getRemoteId());
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
    protected abstract View onCreate(@NonNull Context context);

    @Nullable
    protected abstract Object getValue();

    protected abstract void setErrorMessage(@Nullable String message);

    /**
     * @return an error message if invalid, {@link Optional#empty()} otherwise.
     */
    @NonNull
    public Optional<String> validate() {
        return Optional.empty();
    }
}
