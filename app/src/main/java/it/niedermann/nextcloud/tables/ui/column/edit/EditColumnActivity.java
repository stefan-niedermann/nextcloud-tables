package it.niedermann.nextcloud.tables.ui.column.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityEditColumnBinding;
import it.niedermann.nextcloud.tables.types.manager.type.ColumnManageView;
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

    @Nullable
    private ColumnManageView columnManageView;

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
            binding.typeSelection.setVisibility(View.VISIBLE);
        } else {
            binding.typeSelection.setVisibility(View.GONE);
            binding.toolbar.setTitle(getString(R.string.edit_item, column.getTitle()));
            binding.title.setText(column.getTitle());
            binding.description.setText(column.getDescription());
            binding.mandatory.setChecked(column.isMandatory());
        }

        binding.toolbar.setSubtitle(table.getTitleWithEmoji());

        binding.typeSelection.getDataType$().observe(this, dataType -> {
            try {
                final var column = new Column();
                column.setTableId(table.getId());
                column.setType(dataType.getType());
                column.setSubtype(dataType.getSubType());

                columnManageView = dataType.createManager(this, column, getSupportFragmentManager());
                binding.managerHolder.removeAllViews();
                binding.managerHolder.addView(columnManageView);
            } catch (Exception e) {
                ExceptionDialogFragment.newInstance(e, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
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
                if (columnManageView == null) {
                    Snackbar.make(binding.getRoot(), R.string.column_type_is_required, Snackbar.LENGTH_SHORT).show();
                    return false;
                }

                this.column = columnManageView.getColumn();
            }

            // TODO validate title not null
            this.column.setTitle(Objects.requireNonNullElse(binding.title.getText(), "").toString());
            this.column.setDescription(Objects.requireNonNullElse(binding.description.getText(), "").toString());
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
