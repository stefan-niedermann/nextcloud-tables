package it.niedermann.nextcloud.tables.util;

import static androidx.core.util.TypedValueCompat.spToPx;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

public class DimensionUtil {

    private DimensionUtil() {
        // Util class
    }

    /// [Source](https://chrisdavies.github.io/sp-to-em/)
    @Px
    public static int emToPx(@NonNull Context context, float em) {
        return (int) (spToPx(em / 0.0624f, context.getResources().getDisplayMetrics()));
    }
}
