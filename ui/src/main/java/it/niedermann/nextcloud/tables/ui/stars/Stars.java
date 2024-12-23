package it.niedermann.nextcloud.tables.ui.stars;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.ui.R;
import it.niedermann.nextcloud.tables.ui.databinding.ViewStarsBinding;
import it.niedermann.nextcloud.tables.ui.databinding.ViewStarsSingleBinding;

public class Stars extends FrameLayout {

    private static final int defaultValue = 0;
    private static final int defaultMax = 5;
    private static final boolean defaultReadonly = false;
    private static final boolean defaultResettable = true;
    private static final boolean defaultEnabled = true;

    private final ViewStarsBinding binding;

    private int value;
    private int stars;
    private boolean readonly;
    private boolean resettable;

    public Stars(Context context) {
        this(context, null);
    }

    public Stars(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = ViewStarsBinding.inflate(LayoutInflater.from(context));

        try (final var a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Stars, 0, 0)) {

            value = a.getInteger(R.styleable.Stars_value, defaultValue);
            stars = a.getInteger(R.styleable.Stars_max, defaultMax);
            readonly = a.getBoolean(R.styleable.Stars_readonly, defaultReadonly);
            resettable = a.getBoolean(R.styleable.Stars_resettable, defaultResettable);
            setEnabled(a.getBoolean(R.styleable.Stars_enabled, defaultEnabled));
        }

        binding.clear.setOnClickListener(v -> {
            if (!resettable) {
                throw new IllegalStateException("Failed to clear stars because resettable is false.");
            }

            if (isEnabled() && !readonly) {
                setValue(0);
            }
        });

        bind(value, stars, readonly, resettable, isEnabled());
        addView(binding.getRoot());
    }

    public int getValue() {
        return value;
    }

    /// Value is the number of selected stars
    public void setValue(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value " + value + " must be greater or equal to 0");
        }

        if (value > stars) {
            throw new IllegalArgumentException("value " + value + " must be lower or equal to  " + stars);
        }

        this.value = value;
        this.bind(value, stars, readonly, resettable, isEnabled());
    }

    /// Max must be greater than `0`. Lowers current value if necessary to match new max.
    public void setStars(int stars) {
        if (value > stars) {
            value = stars;
        }

        this.stars = stars;
        this.bind(value, stars, readonly, resettable, isEnabled());
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        this.bind(value, stars, readonly, resettable, isEnabled());
    }

    public void setResettable(boolean resettable) {
        this.resettable = resettable;
        this.bind(value, stars, readonly, resettable, isEnabled());
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final var parentState = super.onSaveInstanceState();
        final var args = new Bundle();
        args.putParcelable("parent", parentState);
        args.putInt("value", value);
        args.putInt("max", stars);
        args.putBoolean("readonly", readonly);
        args.putBoolean("resettable", readonly);
        return parentState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle args) {
            super.onRestoreInstanceState(args.getParcelable("parent"));

            value = args.getInt("value", defaultValue);
            stars = args.getInt("max", defaultMax);
            readonly = args.getBoolean("readonly", defaultReadonly);
            resettable = args.getBoolean("resettable", defaultResettable);

            bind(value, stars, readonly, resettable, isEnabled());

        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void bind(int value,
                      int stars,
                      boolean readonly,
                      boolean resettable,
                      boolean enabled) {

        binding.clear.setVisibility(resettable ? View.VISIBLE : View.GONE);

        final var oldStarCount = binding.stars.getChildCount();
        final var starCountDiff = stars - oldStarCount;

        if (starCountDiff <= 0) {
            if (starCountDiff < 0) {
                binding.stars.removeViews(stars, Math.abs(starCountDiff));
            }

            for (int star = stars; star > 0; star--) {
                final var starIndex = star - 1;
                final var binding = ViewStarsSingleBinding.bind(this.binding.stars.getChildAt(starIndex));
                binding.getRoot().setSelected(star <= value);
            }

        } else {
            for (int star = 1; star <= stars; star++) {
                final ViewStarsSingleBinding binding;

                if (star > oldStarCount) {
                    binding = ViewStarsSingleBinding.inflate(LayoutInflater.from(getContext()));
                    binding.getRoot().setContentDescription(getContext().getString(R.string.stars_content_description_template, star, stars));
                    int finalStar = star;
                    binding.getRoot().setOnClickListener(v -> {
                        if (enabled && !readonly) {
                            setValue(finalStar);
                        }
                    });
                    this.binding.stars.addView(binding.getRoot());

                } else {
                    final var starIndex = star - 1;
                    binding = ViewStarsSingleBinding.bind(this.binding.stars.getChildAt(starIndex));
                }

                binding.getRoot().setSelected(star <= value);
            }
        }
    }
}
