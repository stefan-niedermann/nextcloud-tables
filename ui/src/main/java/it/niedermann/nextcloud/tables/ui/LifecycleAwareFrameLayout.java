package it.niedermann.nextcloud.tables.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public abstract class LifecycleAwareFrameLayout extends FrameLayout implements LifecycleOwner {

    private final LifecycleRegistry lifecycleRegistry;

    public LifecycleAwareFrameLayout(Context context) {
        super(context);
        this.lifecycleRegistry = initializeLifecycle();
    }

    public LifecycleAwareFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.lifecycleRegistry = initializeLifecycle();
    }


    public LifecycleAwareFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.lifecycleRegistry = initializeLifecycle();
    }

    public LifecycleAwareFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.lifecycleRegistry = initializeLifecycle();
    }

    @NonNull
    private LifecycleRegistry initializeLifecycle() {
        final var lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        lifecycleRegistry.setCurrentState(Lifecycle.State.INITIALIZED);
        this.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
            }
        });

        return lifecycleRegistry;
    }

    @NonNull
    @Override
    public final Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
