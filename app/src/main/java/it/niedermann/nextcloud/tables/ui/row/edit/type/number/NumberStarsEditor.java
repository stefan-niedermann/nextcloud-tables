package it.niedermann.nextcloud.tables.ui.row.edit.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.TablesApplication;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.Value;
import it.niedermann.nextcloud.tables.databinding.EditNumberStarsBinding;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;

public class NumberStarsEditor extends DataEditView<EditNumberStarsBinding> {

    private static final String TAG = NumberStarsEditor.class.getSimpleName();

    private final Range<Integer> validRange = new Range<>(0, 5);
    @IntRange(from = 0, to = 5)
    protected int value = 0;

    public NumberStarsEditor(@NonNull Context context) {
        super(context, EditNumberStarsBinding.inflate(LayoutInflater.from(context)));
    }

    public NumberStarsEditor(@NonNull Context context,
                             @Nullable AttributeSet attrs) {
        super(context, attrs, EditNumberStarsBinding.inflate(LayoutInflater.from(context)));
    }

    public NumberStarsEditor(@NonNull Context context,
                             @NonNull Column column) {
        super(context, EditNumberStarsBinding.inflate(LayoutInflater.from(context)), column);

        binding.title.setText(column.getTitle());

        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var star = i + 1;
            binding.stars.getChildAt(i).setOnClickListener(v -> setValue(star));
        }
    }

    @Override
    @Nullable
    public FullData getFullData() {
        Optional.ofNullable(fullData)
                .map(FullData::getData)
                .map(Data::getValue)
                .ifPresent(val -> val.setDoubleValue((double) value));

        return fullData;
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        final var value = Optional
                .ofNullable(fullData.getData())
                .map(Data::getValue)
                .map(Value::getDoubleValue)
                .map(Math::round)
                .map(Long::intValue)
                .orElse(0);

        setValue(value);
    }

    private void setValue(int value) {
        if (validRange.contains(value)) {
            this.value = value;
        } else {
            this.value = 0;

            final var warnMessage = "Value must between " + validRange.getLower() + " and " + validRange.getUpper() + " but was " + value;

            if (TablesApplication.FeatureToggle.STRICT_MODE.enabled) {
                throw new IllegalArgumentException(warnMessage);

            } else {
                Log.w(TAG, warnMessage);
            }
        }

        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var imageButton = (ImageButton) binding.stars.getChildAt(i);
            imageButton.setImageResource(i < this.value ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);
        }
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        // TODO
    }
}
