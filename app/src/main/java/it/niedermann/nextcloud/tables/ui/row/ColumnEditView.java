package it.niedermann.nextcloud.tables.ui.row;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.model.types.EDataType;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.row.type.UnknownEditor;
import it.niedermann.nextcloud.tables.ui.row.type.datetime.DateEditor;
import it.niedermann.nextcloud.tables.ui.row.type.datetime.DateTimeEditor;
import it.niedermann.nextcloud.tables.ui.row.type.datetime.TimeEditor;
import it.niedermann.nextcloud.tables.ui.row.type.number.NumberEditor;
import it.niedermann.nextcloud.tables.ui.row.type.number.ProgressEditor;
import it.niedermann.nextcloud.tables.ui.row.type.number.StarsEditor;
import it.niedermann.nextcloud.tables.ui.row.type.selection.CheckEditor;
import it.niedermann.nextcloud.tables.ui.row.type.selection.MultiEditor;
import it.niedermann.nextcloud.tables.ui.row.type.selection.SelectionEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextLineEditor;
import it.niedermann.nextcloud.tables.ui.row.type.text.TextLinkEditor;

public abstract class ColumnEditView extends FrameLayout {

    protected static final String KEY_DATA = "data";

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

        if (state instanceof Bundle) {
            final var bundle = (Bundle) state;

            if (bundle.containsKey(KEY_DATA)) {
                this.data = (Data) bundle.getSerializable(KEY_DATA);
                setValue(this.data.getValue());
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

    @Nullable
    protected abstract String getValue();

    protected abstract void setValue(@Nullable String value);

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

    public static class Factory {

        private static final String DEFAULT_NOW = "now";
        private static final String DEFAULT_TODAY = "today";

        @NonNull
        public ColumnEditView create(@NonNull EDataType dataType,
                                     @NonNull Context context,
                                     @NonNull Column column,
                                     @Nullable Data data,
                                     @Nullable FragmentManager fragmentManager) {
            final Data dataToPass = ensureDataObjectPresent(column, data);

            switch (dataType) {
                case UNKNOWN:
                    return new UnknownEditor(context, fragmentManager, column, dataToPass);
                case TEXT_LINE:
                    return new TextLineEditor(context, column, dataToPass);
                case TEXT_LINK:
                    return new TextLinkEditor(context, column, dataToPass);
                case DATETIME_DATETIME:
                case DATETIME:
                    return new DateTimeEditor(context, fragmentManager, column, dataToPass);
                case DATETIME_DATE:
                    return new DateEditor(context, fragmentManager, column, dataToPass);
                case DATETIME_TIME:
                    return new TimeEditor(context, fragmentManager, column, dataToPass);
                case NUMBER:
                    return new NumberEditor(context, column, dataToPass);
                case NUMBER_STARS:
                    return new StarsEditor(context, column, dataToPass);
                case NUMBER_PROGRESS:
                    return new ProgressEditor(context, column, dataToPass);
                case SELECTION:
                    return new SelectionEditor(context, column, dataToPass);
                case SELECTION_MULTI:
                    return BuildConfig.DEBUG
                            ? new MultiEditor(context, column, dataToPass)
                            : new UnknownEditor(context, fragmentManager, column, dataToPass);
                case SELECTION_CHECK:
                    return new CheckEditor(context, column, dataToPass);
                case TEXT:
                case TEXT_RICH:
                case TEXT_LONG:
                default:
                    return new TextEditor(context, null, column, dataToPass);
            }
        }

        /**
         * Ensures the given data property is not null. In case its value is null, the value will be
         * initialized with the default value according the given column.
         */
        @NonNull
        private Data ensureDataObjectPresent(@NonNull Column column, @Nullable Data data) {
            final Data dataToPass;

            if (data != null) {
                dataToPass = data;

                final var value = data.getValue();
                if (value == null) {
                    dataToPass.setValue(getDefaultValueByType(column));
                }

            } else {
                dataToPass = new Data();
                dataToPass.setAccountId(column.getAccountId());
                dataToPass.setColumnId(column.getId());
                dataToPass.setRemoteColumnId(column.getRemoteId());
                dataToPass.setValue(getDefaultValueByType(column));
            }

            return dataToPass;
        }

        @Nullable
        private String getDefaultValueByType(@NonNull Column column) {
            switch (column.getType()) {
                case "text":
                    return column.getTextDefault();
                case "number": {
                    final var numberDefault = column.getNumberDefault();
                    return numberDefault == null ? null : String.valueOf(numberDefault);
                }
                case "selection":
                    return column.getSelectionDefault();
                case "datetime": {
                    final var dateTimeDefault = column.getDatetimeDefault();
                    switch (column.getSubtype()) {
                        case "":
                        case "datetime":
                            return DEFAULT_NOW.equals(dateTimeDefault)
                                    ? LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) : null;
                        case "date":
                            return DEFAULT_TODAY.equals(dateTimeDefault)
                                    ? LocalDate.now().format(DateTimeFormatter.ISO_DATE) : null;
                        case "time":
                            return DEFAULT_NOW.equals(dateTimeDefault)
                                    ? LocalTime.now().format(DateTimeFormatter.ISO_TIME) : null;
                        default: {
                            if (BuildConfig.DEBUG) {
                                throw new UnsupportedOperationException("Unexpected column " + column.getType() + " subtype: " + column.getSubtype());
                            }

                            return null;
                        }
                    }
                }
                default: {
                    if (BuildConfig.DEBUG) {
                        throw new UnsupportedOperationException("Unexpected column type " + column.getType());
                    }

                    return null;
                }
            }
        }
    }
}
