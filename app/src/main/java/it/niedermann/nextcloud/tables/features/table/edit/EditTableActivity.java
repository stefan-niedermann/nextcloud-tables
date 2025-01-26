package it.niedermann.nextcloud.tables.features.table.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityEditTableBinding;
import it.niedermann.nextcloud.tables.features.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.features.exception.ExceptionHandler;

public class EditTableActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    @Nullable
    private Table table;
    private Account account;
    private ActivityEditTableBinding binding;
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
        binding = ActivityEditTableBinding.inflate(getLayoutInflater());

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollView, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        if (table != null) {
            binding.emoji.setText(table.getEmoji());
            binding.title.setText(table.getTitle());
            binding.description.setText(table.getDescription());
        }

        editTableViewModel = new ViewModelProvider(this).get(EditTableViewModel.class);

        binding.toolbar.setTitle(table == null
                ? getString(R.string.add_table)
                : getString(R.string.edit_item, table.getTitleWithEmoji())
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_table, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (table == null) {
                table = new Table();
            }

            table.setTitle(Optional.ofNullable(binding.title.getText()).map(Object::toString).orElse(""));
            table.setEmoji(Optional.ofNullable(binding.emoji.getText()).map(Object::toString).orElse(""));
            table.setDescription(Optional.ofNullable(binding.description.getText()).map(Object::toString).orElse(""));

            final var futureResult = table.getRemoteId() == null
                    ? editTableViewModel.createTable(account, table)
                    : editTableViewModel.updateTable(account, table);

            futureResult.whenCompleteAsync((result, exception) -> {
                if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                    ExceptionDialogFragment.newInstance(exception, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }, ContextCompat.getMainExecutor(this));

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account, @Nullable Table table) {
        return createIntent(context, account).putExtra(KEY_TABLE, table);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, EditTableActivity.class)
                .putExtra(KEY_ACCOUNT, account);
    }
}
