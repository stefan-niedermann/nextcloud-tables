package it.niedermann.nextcloud.tables.ui.manageaccounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.tables.databinding.ActivityManageAccountsBinding;


public class ManageAccountsActivity extends AppCompatActivity {

    private static final String TAG = ManageAccountsActivity.class.getSimpleName();

    private ActivityManageAccountsBinding binding;
    private ManageAccountsViewModel viewModel;
    private ManageAccountAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityManageAccountsBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(ManageAccountsViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        adapter = new ManageAccountAdapter(account -> viewModel.setCurrentAccount(account), accountPair -> {
            if (accountPair.first != null) {
                viewModel.deleteAccount(accountPair.first);
            } else {
                throw new IllegalArgumentException("Could not delete account because given account was null.");
            }
            viewModel.setCurrentAccount(accountPair.second);
        });
        binding.accounts.setAdapter(adapter);


        viewModel.getCurrentAccount().observe(this, adapter::setCurrentAccount);
        viewModel.getAccounts().observe(this, localAccounts -> {
            if (localAccounts.size() == 0) {
                Log.i(TAG, "No accounts, finishing " + ManageAccountsActivity.class.getSimpleName());
                finish();
            } else {
                adapter.setAccounts(localAccounts);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, ManageAccountsActivity.class);
    }
}
