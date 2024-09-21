package it.niedermann.nextcloud.tables.ui.row;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collection;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityEditRowBinding;
import it.niedermann.nextcloud.tables.model.EDataType;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.tables.ui.row.type.UnknownEditor;

public class EditRowActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    private static final String KEY_ROW = "row";
    private static final String KEY_DUPLICATE = "duplicate";
    private Account account;
    private Table table;
    private Row row;
    private boolean duplicate;
    private EditRowViewModel editRowViewModel;
    private ActivityEditRowBinding binding;
    private final Collection<ColumnEditView> editors = new ArrayList<>();

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
        this.row = (Row) intent.getSerializableExtra(KEY_ROW);
        this.duplicate = intent.getBooleanExtra(KEY_DUPLICATE, false);

        binding = ActivityEditRowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (this.duplicate) {
            binding.toolbar.setTitle(R.string.duplicate_row);
        } else if (row == null) {
            binding.toolbar.setTitle(R.string.add_row);
        } else {
            binding.toolbar.setTitle(R.string.edit_row);
        }
        binding.toolbar.setSubtitle(table.getTitleWithEmoji());

        editRowViewModel = new ViewModelProvider(this).get(EditRowViewModel.class);

        final var editViewFactory = new ColumnEditView.Factory();
        editRowViewModel.getNotDeletedColumns(table).thenAcceptAsync(columns -> {
            binding.columns.removeAllViews();
            editors.clear();
            editRowViewModel.getData(row).thenAcceptAsync(values -> {
                for (final var column : columns) {
                    try {
                        final var editor = editViewFactory.create(EDataType.findByColumn(column),
                                this,
                                column,
                                values.get(column.getId()),
                                getSupportFragmentManager());
                        binding.columns.addView(editor);
                        editors.add(editor);
                    } catch (Exception e) {
                        e.printStackTrace();

                        final var layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final var unknownEditor = new UnknownEditor(this,
                                getSupportFragmentManager(),
                                column,
                                editViewFactory.ensureDataObjectPresent(column, values.get(column.getId())));
                        binding.columns.addView(unknownEditor);
                        unknownEditor.setErrorMessage(getString(R.string.could_not_display_column_editor, column.getTitle()));

                        final var btn = new MaterialButton(this);
                        btn.setText(R.string.simple_exception);
                        btn.setOnClickListener(v -> ExceptionDialogFragment.newInstance(e, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                        layout.addView(btn);
                        binding.columns.addView(layout);
                    }
                }
            }, ContextCompat.getMainExecutor(this));
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_row, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            final var futureResult = duplicate || row == null
                    ? editRowViewModel.createRow(account, table, editors)
                    : editRowViewModel.updateRow(account, table, row, editors);

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

    public static Intent createAddIntent(@NonNull Context context, @NonNull Account account, @NonNull Table table) {
        return new Intent(context, EditRowActivity.class)
                .putExtra(KEY_ACCOUNT, account)
                .putExtra(KEY_TABLE, table);
    }

    public static Intent createEditIntent(@NonNull Context context, @NonNull Account account, @NonNull Table table, @NonNull Row row) {
        return createAddIntent(context, account, table)
                .putExtra(KEY_ROW, row);
    }

    /**
     * If {@param duplicate} is <code>true</code>, it will open the activity in edit mode prepopulating the information from the given {@param row}.
     */
    public static Intent createDuplicateIntent(@NonNull Context context, @NonNull Account account, @NonNull Table table, @NonNull Row row) {
        return createEditIntent(context, account, table, row)
                .putExtra(KEY_DUPLICATE, true);
    }
}
