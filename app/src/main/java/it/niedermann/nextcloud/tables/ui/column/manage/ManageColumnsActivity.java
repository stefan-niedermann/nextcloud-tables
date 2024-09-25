package it.niedermann.nextcloud.tables.ui.column.manage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.TablesApplication.FeatureToggle;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityManageColumnsBinding;
import it.niedermann.nextcloud.tables.ui.column.edit.EditColumnActivity;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;

public class ManageColumnsActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    private Account account;
    private Table table;
    private ManageColumnsViewModel manageColumnsViewModel;
    private ActivityManageColumnsBinding binding;

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

        manageColumnsViewModel = new ViewModelProvider(this).get(ManageColumnsViewModel.class);
        binding = ActivityManageColumnsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final var adapter = new ManageColumnsAdapter(column -> {
            if (FeatureToggle.EDIT_COLUMN.enabled) {
                startActivity(EditColumnActivity.createIntent(this, account, table, column));
            } else {
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });

        // TODO Waiting for https://github.com/nextcloud/tables/issues/607
//        final var touchHelper = new ManageColumnsTouchHelper(
//                adapter,
//                reorderedIds -> manageColumnsViewModel.reorderColumns(account, table.getId(), reorderedIds).whenCompleteAsync((result, exception) -> {
//                    if (exception != null) {
//                        ExceptionDialogFragment.newInstance(exception, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
//                    }
//                }, ContextCompat.getMainExecutor(this))
//        );

//        touchHelper.attachToRecyclerView(binding.recyclerView);

        binding.recyclerView.setAdapter(adapter);
        manageColumnsViewModel.getNotDeletedColumns$(table).observe(this, adapter::setItems);
        binding.fab.setOnClickListener(v -> {
            if (FeatureToggle.CREATE_COLUMN.enabled) {
                startActivity(EditColumnActivity.createIntent(this, account, table));
            } else {
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account, @NonNull Table table) {
        return new Intent(context, ManageColumnsActivity.class)
                .putExtra(KEY_ACCOUNT, account)
                .putExtra(KEY_TABLE, table);
    }
}
