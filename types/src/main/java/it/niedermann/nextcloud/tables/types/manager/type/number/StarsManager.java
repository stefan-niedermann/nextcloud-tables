package it.niedermann.nextcloud.tables.types.manager.type.number;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.R;
import it.niedermann.nextcloud.tables.types.databinding.ManageNumberStarsBinding;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;

public class StarsManager extends ColumnManageView {

    protected ManageNumberStarsBinding binding;

    public StarsManager(@NonNull Context context) {
        super(context);
    }

    public StarsManager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StarsManager(@NonNull Context context, @NonNull Column column, @Nullable FragmentManager fragmentManager) {
        super(context, column, fragmentManager);
    }

    @NonNull
    @Override
    protected View onCreate(@NonNull Context context) {
        binding = ManageNumberStarsBinding.inflate(LayoutInflater.from(context));

        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var star = i + 1;
            binding.stars.getChildAt(i).setOnClickListener(v -> setValue(star));
        }

        return binding.getRoot();
    }

    @Override
    protected void setColumn(@NonNull Column column) {
        super.setColumn(column);
        final var value = column.getNumberDefault();

        // https://github.com/nextcloud/tables/issues/1385
        final var validRange = new Range<>(1, 5);
        if (value != null && validRange.contains(value.intValue())) {
            setValue(value.intValue());
        }
    }

    private void setValue(int stars) {
        this.column.setNumberDefault((double) stars);

        for (int i = 0; i < binding.stars.getChildCount(); i++) {
            final var imageButton = (ImageButton) binding.stars.getChildAt(i);
            imageButton.setImageResource(i < stars ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);
        }
    }
}
