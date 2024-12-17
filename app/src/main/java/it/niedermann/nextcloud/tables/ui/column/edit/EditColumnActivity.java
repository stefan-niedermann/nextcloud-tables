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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ActivityEditColumnBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;

public class EditColumnActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    private static final String KEY_COLUMN = "column";
    private Account account;
    private Table table;
    private FullColumn fullColumn;
    private EditColumnViewModel editColumnViewModel;
    private ActivityEditColumnBinding binding;
    private ManageDataTypeServiceRegistry registry;

    @Nullable
    private ColumnEditView columnEditView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final var intent = getIntent();

        if (intent == null || !intent.hasExtra(KEY_ACCOUNT) || !intent.hasExtra(KEY_TABLE)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_TABLE + " must be provided.");
        }

        this.registry = new ManageDataTypeServiceRegistry();
        this.account = (Account) intent.getSerializableExtra(KEY_ACCOUNT);
        this.table = (Table) intent.getSerializableExtra(KEY_TABLE);
        this.fullColumn = (FullColumn) intent.getSerializableExtra(KEY_COLUMN);

        editColumnViewModel = new ViewModelProvider(this).get(EditColumnViewModel.class);
        binding = ActivityEditColumnBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setSubtitle(table.getTitleWithEmoji());

        if (fullColumn == null) {
            binding.toolbar.setTitle(R.string.add_column);
            binding.typeSelection.setVisibility(View.VISIBLE);

            binding.typeSelection.getDataType$().observe(this, dataType -> {
                try {
                    final var column = new Column();
                    column.setTableId(table.getId());
                    column.setDataType(dataType);

                    final var fullColumn = new FullColumn(column);

                    columnEditView = createManageView(fullColumn);
                    columnEditView.setEnabled(false);
                    binding.managerHolder.removeAllViews();
                    binding.managerHolder.addView(columnEditView);
                } catch (Exception e) {
                    ExceptionDialogFragment.newInstance(e, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            });
        } else {
            final var column = fullColumn.getColumn();

            binding.typeSelection.setVisibility(View.GONE);
            binding.toolbar.setTitle(getString(R.string.edit_item, column.getTitle()));
            binding.title.setText(column.getTitle());
            binding.description.setText(column.getDescription());
            binding.mandatory.setChecked(column.isMandatory());
            columnEditView = createManageView(fullColumn);
            binding.managerHolder.addView(columnEditView);
        }
    }

    private ColumnEditView<? extends ViewBinding> createManageView(@NonNull FullColumn fullColumn) {
        final var editView = registry.getService(fullColumn.getColumn().getDataType())
                .create(this, getSupportFragmentManager());
        editView.setFullColumn(fullColumn);
        return editView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_column, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (this.fullColumn == null) {
                if (columnEditView == null) {
                    Snackbar.make(binding.getRoot(), R.string.column_type_is_required, Snackbar.LENGTH_SHORT).show();
                    return false;
                }

                this.fullColumn = columnEditView.getFullColumn();
            }

            // TODO validate title not null
            final var column = fullColumn.getColumn();
            column.setTitle(Objects.requireNonNullElse(binding.title.getText(), "").toString());
            column.setDescription(Objects.requireNonNullElse(binding.description.getText(), "").toString());
            column.setMandatory(binding.mandatory.isChecked());

            final var futureResult = column.getRemoteId() == null
                    ? editColumnViewModel.createColumn(account, table, fullColumn.getColumn())
                    : editColumnViewModel.updateColumn(account, table, fullColumn.getColumn());

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

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull Account account,
                                      @NonNull Table table) {
        return new Intent(context, EditColumnActivity.class)
                .putExtra(KEY_TABLE, table)
                .putExtra(KEY_ACCOUNT, account);
    }

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull Account account,
                                      @NonNull Table table,
                                      @NonNull FullColumn fullColumn) {
        return createIntent(context, account, table)
                .putExtra(KEY_COLUMN, fullColumn);
    }
}
