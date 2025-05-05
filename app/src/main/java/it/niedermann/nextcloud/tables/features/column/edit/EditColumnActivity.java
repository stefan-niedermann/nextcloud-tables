package it.niedermann.nextcloud.tables.features.column.edit;

import static java.util.Objects.requireNonNullElse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.snackbar.Snackbar;

import java.util.stream.Stream;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.databinding.ActivityEditColumnBinding;
import it.niedermann.nextcloud.tables.features.column.edit.types.ColumnEditView;
import it.niedermann.nextcloud.tables.features.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.features.exception.ExceptionHandler;

public class EditColumnActivity extends AppCompatActivity {

    private static final String TAG = EditColumnActivity.class.getSimpleName();

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
    private ColumnEditView<?> columnEditView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        final var intent = getIntent();

        if (intent == null || !intent.hasExtra(KEY_ACCOUNT) || !intent.hasExtra(KEY_TABLE)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " and " + KEY_TABLE + " must be provided.");
        }

        editColumnViewModel = new ViewModelProvider(this).get(EditColumnViewModel.class);
        binding = ActivityEditColumnBinding.inflate(getLayoutInflater());

        this.registry = new ManageDataTypeServiceRegistry(
                accountId -> editColumnViewModel.getSearchProvider(accountId)
        );
        this.account = (Account) intent.getSerializableExtra(KEY_ACCOUNT);
        this.table = (Table) intent.getSerializableExtra(KEY_TABLE);
        this.fullColumn = (FullColumn) intent.getSerializableExtra(KEY_COLUMN);

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
            mlp.topMargin = 0;
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        binding.toolbar.setSubtitle(table.getTitleWithEmoji());

        if (fullColumn == null) {
            binding.toolbar.setTitle(R.string.add_column);
            binding.typeSelection.setVisibility(View.VISIBLE);
            binding.typeSelection.setOnChangeListener(dataType -> {

                try {
                    final var column = new Column();
                    column.setTableId(table.getId());
                    column.setDataType(dataType);

                    final var fullColumn = new FullColumn(column);

                    columnEditView = createManageView(fullColumn);
                    binding.managerHolder.removeAllViews();
                    binding.managerHolder.addView(columnEditView);
                } catch (Exception e) {
                    ExceptionDialogFragment.newInstance(e, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }

            });
        } else {
            final var column = fullColumn.getColumn();
            final var dataTypeSupportsEditing = column.getDataType().supportsEditing();

            binding.toolbar.setTitle(getString(R.string.edit_item, column.getTitle()));

            if (dataTypeSupportsEditing) {
                binding.experimentalFeature.setVisibility(View.VISIBLE);
                binding.unsupportedColumn.setVisibility(View.GONE);

            } else {
                binding.experimentalFeature.setVisibility(View.GONE);
                binding.unsupportedColumn.setText(getString(R.string.unsupported_column_type, column.getDataType().toHumanReadableString(this)));
                binding.unsupportedColumn.setVisibility(View.VISIBLE);
            }

            binding.title.setText(column.getTitle());
            binding.description.setText(column.getDescription());
            binding.mandatory.setChecked(column.isMandatory());
            binding.typeSelection.setVisibility(View.GONE);

            Stream.of(binding.title, binding.description, binding.mandatory)
                    .forEach(view -> view.setEnabled(dataTypeSupportsEditing));

            columnEditView = createManageView(fullColumn);
            columnEditView.setEnabled(dataTypeSupportsEditing);
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
        if (fullColumn == null || fullColumn.getColumn().getDataType().supportsEditing()) {
            getMenuInflater().inflate(R.menu.menu_edit_column, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (columnEditView == null) {
                Snackbar.make(binding.getRoot(), R.string.column_type_is_required, Snackbar.LENGTH_SHORT).show();
                return false;
            } else {
                // We have to call getFullColumn() when creating or updating because ColumnEditView will only write values to column when invoking the getter
                fullColumn = columnEditView.getFullColumn();
            }

            // TODO validate title not null
            final var column = fullColumn.getColumn();
            column.setTitle(requireNonNullElse(binding.title.getText(), "").toString());
            column.setDescription(requireNonNullElse(binding.description.getText(), "").toString());
            column.setMandatory(binding.mandatory.isChecked());

            final var futureResult = column.getRemoteId() == null
                    ? editColumnViewModel.createColumn(account, table, fullColumn)
                    // TODO If UPDATE column and TYPE = SELECTION MULTI/SINGLE, track synchronization and post notification in case synchronization was not successful to avoid future conflicts with selection option IDs
                    : editColumnViewModel.updateColumn(account, table, fullColumn);

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
