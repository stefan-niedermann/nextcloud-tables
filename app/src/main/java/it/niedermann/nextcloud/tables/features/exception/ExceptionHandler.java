package it.niedermann.nextcloud.tables.features.exception;

import android.app.Activity;

import androidx.annotation.NonNull;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @NonNull
    private final Activity activity;

    public ExceptionHandler(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable throwable) {
        try {
            throwable.printStackTrace();
        } catch (NullPointerException ignored) {
        }

        activity.getApplicationContext().startActivity(ExceptionActivity.createIntent(activity, throwable));
        activity.finish();

        // This prevents the new activity from being created since at least API 35, therefore commenting it
        // Runtime.getRuntime().exit(0);
    }
}
