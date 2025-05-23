package it.niedermann.nextcloud.tables.features.column.manage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityManageColumnsBinding;
import it.niedermann.nextcloud.tables.features.column.edit.EditColumnActivity;
import it.niedermann.nextcloud.tables.features.exception.ExceptionHandler;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.fab, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            final var defaultMargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);
            mlp.topMargin = insets.top + defaultMargin;
            mlp.leftMargin = insets.left + defaultMargin;
            mlp.bottomMargin = insets.bottom + defaultMargin;
            mlp.rightMargin = insets.right + defaultMargin;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        final var adapter = new ManageColumnsAdapter(fullColumn -> startActivity(EditColumnActivity.createIntent(this, account, table, fullColumn)));

        // TODO Waiting for https://github.com/nextcloud/tables/issues/607
//        final var touchHelper = new ManageColumnsTouchHelper(
//                adapter,
//                reorderedIds -> manageColumnsViewModel.reorderColumns(account, table.getId(), reorderedIds).whenCompleteAsync((result, exception) -> {
//                    if (exception != null && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
//                        ExceptionDialogFragment.newInstance(exception, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
//                    }
//                }, ContextCompat.getMainExecutor(this))
//        );

//        touchHelper.attachToRecyclerView(binding.recyclerView);

        binding.recyclerView.setAdapter(adapter);
        manageColumnsViewModel.getNotDeletedFullColumns$(table).observe(this, adapter::setItems);
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
