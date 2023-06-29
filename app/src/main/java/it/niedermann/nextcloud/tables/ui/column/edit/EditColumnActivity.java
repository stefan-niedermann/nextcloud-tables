package it.niedermann.nextcloud.tables.ui.column.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityEditColumnBinding;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;

public class EditColumnActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    private static final String KEY_COLUMN = "column";
    private Account account;
    private Table table;
    private Column column;
    private EditColumnViewModel editColumnViewModel;
    private ActivityEditColumnBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final var intent = getIntent();

        if (intent == null || !intent.hasExtra(KEY_ACCOUNT) || !intent.hasExtra(KEY_TABLE)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_TABLE + " must be provided.");
        }

        this.account = (Account) intent.getSerializableExtra(KEY_ACCOUNT);
        this.table = (Table) intent.getSerializableExtra(KEY_TABLE);
        this.column = (Column) intent.getSerializableExtra(KEY_COLUMN);

        editColumnViewModel = new ViewModelProvider(this).get(EditColumnViewModel.class);
        binding = ActivityEditColumnBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (column == null) {
            binding.toolbar.setTitle(R.string.add_column);
        } else {
            binding.toolbar.setTitle(getString(R.string.edit_item, column.getTitle()));
            binding.title.setText(column.getTitle());
            binding.description.setText(column.getDescription());
            binding.mandatory.setChecked(column.isMandatory());
        }

        binding.toolbar.setSubtitle(table.getTitleWithEmoji());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_column, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (this.column == null) {
                this.column = new Column();
                this.column.setType("text");
                this.column.setSubtype("line");
                this.column.setTableId(table.getId());
                this.column.setOrderWeight(0);
            }

            // TODO validate title not null
            final var title = binding.title.getText();
            final var description = binding.description.getText();
            this.column.setTitle(title == null ? "" : title.toString());
            this.column.setDescription(description == null ? "" : description.toString());
            this.column.setMandatory(binding.mandatory.isChecked());

            final var futureResult = this.column.getRemoteId() == null
                    ? editColumnViewModel.createColumn(account, table, column)
                    : editColumnViewModel.updateColumn(account, table, column);

            futureResult.whenCompleteAsync((result, exception) -> {
                if (exception != null) {
                    ExceptionDialogFragment.newInstance(exception, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }, ContextCompat.getMainExecutor(this));

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account, @NonNull Table table) {
        return new Intent(context, EditColumnActivity.class)
                .putExtra(KEY_TABLE, table)
                .putExtra(KEY_ACCOUNT, account);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account, @NonNull Table table, @NonNull Column column) {
        return createIntent(context, account, table)
                .putExtra(KEY_COLUMN, column);
    }
}
