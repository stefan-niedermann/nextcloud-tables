package it.niedermann.nextcloud.tables.features.exception;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.exception.ExceptionUtil;
import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.databinding.ActivityExceptionBinding;
import it.niedermann.nextcloud.tables.features.exception.tips.TipsAdapter;

public class ExceptionActivity extends AppCompatActivity {

    private static final String KEY_THROWABLE = "throwable";
    private ActivityExceptionBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExceptionBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.wrapper, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        Throwable throwable = ((Throwable) getIntent().getSerializableExtra(KEY_THROWABLE));

        if (throwable == null) {
            throwable = new Exception("Could not get exception");
        }

        final var adapter = new TipsAdapter(this::startActivity);
        final String debugInfo = getDebugInfo(throwable);

        binding.tips.setAdapter(adapter);
        binding.tips.setNestedScrollingEnabled(false);
        binding.toolbar.setTitle(R.string.simple_exception);
        binding.message.setText(throwable.getMessage());
        binding.stacktrace.setText(debugInfo);
        binding.copy.setOnClickListener((v) -> ClipboardUtil.copyToClipboard(this, getString(R.string.simple_exception), "```\n" + debugInfo + "\n```"));
        binding.close.setOnClickListener((v) -> finish());

        adapter.setThrowable(this, null, throwable);
    }

    private String getDebugInfo(@NonNull Throwable throwable) {
        try {
            return "Full Crash:\n\n" + ExceptionUtil.getDebugInfos(this, throwable, BuildConfig.FLAVOR);
        } catch (NullPointerException e) {
            return "Full Crash:\n\n" + ExceptionUtil.getDebugInfos(this, new Exception(throwable.getMessage()), BuildConfig.FLAVOR);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, Throwable throwable) {
        return new Intent(context, ExceptionActivity.class)
                .putExtra(KEY_THROWABLE, throwable)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
