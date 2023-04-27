package it.niedermann.nextcloud.tables.ui.row;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Row;
import it.niedermann.nextcloud.tables.database.entity.Table;
import it.niedermann.nextcloud.tables.databinding.ActivityEditRowBinding;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;

public class EditRowActivity extends AppCompatActivity {

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_TABLE = "table";
    private static final String KEY_ROW = "row";
    private Account account;
    private Table table;
    private Row row;
    private EditRowViewModel editRowViewModel;
    private ActivityEditRowBinding binding;

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

        binding = ActivityEditRowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editRowViewModel = new ViewModelProvider(this).get(EditRowViewModel.class);
        final var editors = new ArrayList<ColumnEditView>();
        editRowViewModel.getNotDeletedColumns$(table).observe(this, columns -> {
            binding.columns.removeAllViews();
            editors.clear();
            for (final var column : columns) {
                final var type = ColumnEditType.findByType(column.getType(), column.getSubtype());
                final var editor = type.inflate(this, column);
                binding.columns.addView(editor.getView());
                editors.add(editor);
            }
        });


        binding.save.setOnClickListener(v -> {
            editRowViewModel.createRow(account, table, editors);
            finish();
        });
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account, @NonNull Table table) {
        return new Intent(context, EditRowActivity.class)
                .putExtra(KEY_ACCOUNT, account)
                .putExtra(KEY_TABLE, table);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull Account account, @NonNull Table table, @NonNull Row row) {
        return createIntent(context, account, table)
                .putExtra(KEY_ROW, row);
    }
}
