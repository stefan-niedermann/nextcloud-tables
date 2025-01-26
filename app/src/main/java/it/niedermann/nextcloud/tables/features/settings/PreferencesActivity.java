package it.niedermann.nextcloud.tables.features.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.databinding.ActivityPreferencesBinding;
import it.niedermann.nextcloud.tables.features.exception.ExceptionHandler;

public class PreferencesActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private ActivityPreferencesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        if (!getIntent().hasExtra(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided");
        }

        final var account = (Account) getIntent().getSerializableExtra(KEY_ACCOUNT);

        binding = ActivityPreferencesBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        setResult(RESULT_OK);

        getSupportFragmentManager()
                .beginTransaction()
                .add(binding.preferencesFragment.getId(), PreferencesFragment.newInstance(account))
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, PreferencesActivity.class)
                .putExtra(KEY_ACCOUNT, account);
    }
}
