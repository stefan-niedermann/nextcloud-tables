package it.niedermann.nextcloud.tables.ui.table.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityEditTableBinding;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;

public class EditTableActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    @Nullable
    private Table table;
    private Account account;
    private EditTableViewModel editTableViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final var intent = getIntent();

        if (intent == null || !intent.hasExtra(KEY_ACCOUNT)) {
            throw new IllegalStateException(KEY_ACCOUNT + " must be provided");
        }

        account = (Account) intent.getSerializableExtra(KEY_ACCOUNT);
        table = (Table) intent.getSerializableExtra(KEY_TABLE);

        if (table == null) {
            table = new Table();
        }

        final var binding = ActivityEditTableBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        editTableViewModel = new ViewModelProvider(this).get(EditTableViewModel.class);

        if (table == null) {
            binding.toolbar.setTitle(R.string.add_table);
            binding.save.setOnClickListener(v -> {
                final var newTitle = binding.title.getText();
                table.setTitle(newTitle == null ? "" : newTitle.toString());
                editTableViewModel.createTable(account, table);
                finish();
            });
        } else {
            binding.title.setText(table.getTitle());
            binding.save.setOnClickListener(v -> {
                final var newTitle = binding.title.getText();
                table.setTitle(newTitle == null ? "" : newTitle.toString());
                editTableViewModel.updateTable(table);
                finish();
            });
        }
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account, @Nullable Table table) {
        return createIntent(context, account).putExtra(KEY_TABLE, table);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, EditTableActivity.class)
                .putExtra(KEY_ACCOUNT, account);
    }
}
