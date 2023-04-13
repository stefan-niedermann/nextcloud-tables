package it.niedermann.nextcloud.tables.ui.column;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.databinding.ActivityEditColumnBinding;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;

public class EditColumnActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_ROW = "row";
    private Account account;
    private Row row;
    private EditColumnViewModel editColumnViewModel;
    private ActivityEditColumnBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final var intent = getIntent();

        if (intent == null || !intent.hasExtra(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided.");
        }

        this.account = (Account) intent.getSerializableExtra(KEY_ACCOUNT);
        this.row = (Row) intent.getSerializableExtra(KEY_ROW);

        binding = ActivityEditColumnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editColumnViewModel = new ViewModelProvider(this).get(EditColumnViewModel.class);

        binding.save.setOnClickListener(v -> {
            finish();
        });
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, EditColumnActivity.class)
                .putExtra(KEY_ACCOUNT, account);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account, @NonNull Row row) {
        return createIntent(context, account)
                .putExtra(KEY_ROW, row);
    }
}
