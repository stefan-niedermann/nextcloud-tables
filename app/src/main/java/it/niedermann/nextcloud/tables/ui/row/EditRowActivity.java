package it.niedermann.nextcloud.tables.ui.row;

import static java.util.function.Predicate.not;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.databinding.ActivityEditRowBinding;
import it.niedermann.nextcloud.tables.repository.defaults.DataTypeDefaultServiceRegistry;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.tables.ui.row.edit.factories.EditorFactory;
import it.niedermann.nextcloud.tables.ui.row.edit.type.DataEditView;

public class EditRowActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    private static final String KEY_ROW = "row";
    private Account account;
    private Table table;
    private Row row;
    private EditRowViewModel editRowViewModel;
    private ActivityEditRowBinding binding;
    private final Collection<DataEditView<?>> editors = new ArrayList<>();
    private DataTypeServiceRegistry<EditorFactory<? extends ViewBinding>> editorFactoryRegistry;
    private DataTypeServiceRegistry<DefaultValueSupplier> defaultSupplierRegistry;

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
        this.defaultSupplierRegistry = new DataTypeDefaultServiceRegistry();
        this.editorFactoryRegistry = new EditDataTypeServiceRegistry();

        binding = ActivityEditRowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setTitle(row == null ? R.string.add_row : R.string.edit_row);
        binding.toolbar.setSubtitle(table.getTitleWithEmoji());

        if (this.row != null) {
            final var callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (savePromptRequired()) {
                        showSavePromptGuard(EditRowActivity.this::finish);
                    }
                }
            };
            getOnBackPressedDispatcher().addCallback(this, callback);
        }

        editRowViewModel = new ViewModelProvider(this).get(EditRowViewModel.class);

        editRowViewModel.getNotDeletedColumns(table)
                .thenComposeAsync(columns -> {
                    binding.columns.removeAllViews();
                    editors.clear();

                    return editRowViewModel.getFullData(row)
                            .thenApplyAsync(fullDataGrid -> new Pair<>(columns, fullDataGrid));
                }, ContextCompat.getMainExecutor(this))
                .thenAcceptAsync(args -> {
                    final var columns = args.first;
                    final var fullDataGrid = args.second;
                    for (final var column : columns) {
                        try {
                            final var defaultValueSupplier = defaultSupplierRegistry.getService(column.getColumn().getDataType());
                            final var fullData = Optional
                                    .ofNullable(fullDataGrid.get(column.getColumn().getId()))
                                    .map(src -> defaultValueSupplier.ensureDefaultValue(column, src))
                                    .orElse(defaultValueSupplier.ensureDefaultValue(column, null));

                            Optional.ofNullable(row).map(Row::getId).ifPresent(fullData.getData()::setRowId);

                            final var editor = editorFactoryRegistry
                                    .getService(column.getColumn().getDataType())
                                    .create(this, column, getSupportFragmentManager());

                            editor.setFullData(fullData);
                            editor.invalidate();
                            editor.requestLayout();

                            binding.columns.addView(editor);
                            editors.add(editor);
                        } catch (Exception e) {
                            e.printStackTrace();

                            final var layout = new LinearLayout(this);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            try {
                                final var unknownEditor = editorFactoryRegistry
                                        .getService(EDataType.UNKNOWN)
                                        .create(this, column, getSupportFragmentManager());

                                Optional
                                        .ofNullable(fullDataGrid.get(column.getColumn().getId()))
                                        .ifPresent(fullData -> {
                                            unknownEditor.setFullData(fullData);
                                            unknownEditor.invalidate();
                                            unknownEditor.requestLayout();
                                        });

                                unknownEditor.setErrorMessage(getString(R.string.could_not_display_column_editor, column.getColumn().getTitle()));

                                binding.columns.addView(unknownEditor);
                                editors.add(unknownEditor);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            final var btn = new MaterialButton(this);
                            btn.setText(R.string.simple_exception);
                            btn.setOnClickListener(v -> ExceptionDialogFragment.newInstance(e, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                            layout.addView(btn);
                            binding.columns.addView(layout);
                        }
                    }
                }, ContextCompat.getMainExecutor(this)); ;
    }

    private boolean savePromptRequired() {
        return editors.stream().anyMatch(not(DataEditView::isPristine));
    }

    private void showSavePromptGuard(@NonNull Runnable closeFunction) {
        new MaterialAlertDialogBuilder(EditRowActivity.this)
                .setTitle(R.string.unsafed_changes)
                .setMessage(R.string.unsafed_changes_details)
                .setPositiveButton(R.string.simple_save, (dialog, which) -> {
                    save();
                    closeFunction.run();
                })
                .setNegativeButton(R.string.simple_discard, (dialog, which) -> closeFunction.run())
                .setNeutralButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void save() {
        final var futureResult = row == null
                ? editRowViewModel.createRow(account, table, editors)
                : editRowViewModel.updateRow(account, table, row, editors);

        futureResult.whenCompleteAsync((result, exception) -> {
            if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                ExceptionDialogFragment.newInstance(exception, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (savePromptRequired()) {
            showSavePromptGuard(super::onSupportNavigateUp);
            return true;
        }

        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_row, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            save();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull Account account,
                                      @NonNull Table table) {
        return new Intent(context, EditRowActivity.class)
                .putExtra(KEY_ACCOUNT, account)
                .putExtra(KEY_TABLE, table);
    }

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull Account account,
                                      @NonNull Table table,
                                      @NonNull Row row) {
        return createIntent(context, account, table)
                .putExtra(KEY_ROW, row);
    }
}
