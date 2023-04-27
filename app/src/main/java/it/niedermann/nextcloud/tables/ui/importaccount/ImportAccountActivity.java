package it.niedermann.nextcloud.tables.ui.importaccount;

import static com.nextcloud.android.sso.AccountImporter.REQUEST_AUTH_TOKEN_SSO;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.databinding.ActivityImportBinding;
import it.niedermann.nextcloud.tables.remote.SyncWorker;
import it.niedermann.nextcloud.tables.remote.exception.AccountAlreadyImportedException;
import it.niedermann.nextcloud.tables.remote.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;

public class ImportAccountActivity extends AppCompatActivity {

    private static final String TAG = ImportAccountActivity.class.getSimpleName();
    private ActivityImportBinding binding;
    private ImportAccountViewModel importAccountViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setTheme(R.style.AppTheme);

        importAccountViewModel = new ViewModelProvider(this).get(ImportAccountViewModel.class);
        binding = ActivityImportBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            binding.image.setClipToOutline(true);
        }

        importAccountViewModel.getImportState().observe(this, this::applyImportState);
        binding.addButton.setOnClickListener(this::onAddButtonClicked);
    }

    private void onAddButtonClicked(@NonNull View addButton) {
        try {
            AccountImporter.pickNewAccount(this);
        } catch (NextcloudFilesAppNotInstalledException e) {
            UiExceptionManager.showDialogForException(this, e);
        } catch (AndroidGetAccountsPermissionNotGranted e) {
            AccountImporter.requestAndroidAccountPermissionsAndPickAccount(this);
        }
    }

    private void applyImportState(@NonNull ImportAccountViewModel.ImportState state) {
        switch (state.state) {
            case IMPORTING_ACCOUNT: {
                setAvatar(state.account);
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.progressText.setVisibility(View.VISIBLE);
                binding.progressText.setText(R.string.import_state_import_account);
                binding.addButton.setEnabled(false);
                break;
            }
            case IMPORTING_TABLES: {
                setAvatar(state.account);
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.progressText.setVisibility(View.VISIBLE);
                binding.progressText.setText(R.string.import_state_import_tables);
                binding.addButton.setEnabled(false);
                break;
            }
            case FINISHED: {
                setAvatar(state.account);
                binding.progressCircular.setVisibility(View.GONE);
                binding.progressText.setVisibility(View.GONE);
                SyncWorker.update(getApplicationContext());
                setResult(RESULT_OK);
                finish();
                break;
            }
            case ERROR: {
                binding.image.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));
                binding.progressCircular.setVisibility(View.GONE);
                binding.progressText.setVisibility(View.VISIBLE);
                binding.addButton.setEnabled(true);

                if (state.error instanceof AccountAlreadyImportedException) {
                    binding.progressText.setText(R.string.account_already_imported);
                } else {
                    if (state.error != null) {
                        state.error.printStackTrace();
                        ExceptionDialogFragment.newInstance(state.error, state.account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());

                        if(state.error instanceof ServerNotAvailableException) {
                            binding.progressText.setText(((ServerNotAvailableException) state.error).getReason().messageRes);
                        } else {
                            binding.progressText.setText(state.error.getMessage());
                        }
                    } else {
                        binding.progressText.setText(R.string.hint_error_appeared);
                        new IllegalStateException("Received error state while importing, but exception was null").printStackTrace();
                    }
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + state.state);
        }
    }

    private void setAvatar(@Nullable Account account) {
        if (account == null) {
            throw new NullPointerException();
        }
        binding.progressText.setText(getString(R.string.importing_account, account.getDisplayName()));
        Glide.with(binding.image)
                .load(account.getAvatarUrl(binding.image.getWidth()))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(binding.image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_AUTH_TOKEN_SSO || resultCode != RESULT_CANCELED) {
            try {
                AccountImporter.onActivityResult(requestCode, resultCode, data, ImportAccountActivity.this,
                        account -> importAccountViewModel.createAccount(new Account(account.name, account.userId, account.url)));
            } catch (AccountImportCancelledException e) {
                Log.i(TAG, "Account import has been canceled.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, ImportAccountActivity.class);
    }
}
