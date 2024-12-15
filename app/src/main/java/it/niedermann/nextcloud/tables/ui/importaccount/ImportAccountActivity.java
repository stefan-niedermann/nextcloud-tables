package it.niedermann.nextcloud.tables.ui.importaccount;

import static com.nextcloud.android.sso.AccountImporter.REQUEST_AUTH_TOKEN_SSO;
import static com.nextcloud.android.sso.AccountImporter.REQUEST_GET_ACCOUNTS_PERMISSION;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.exceptions.SSOException;
import com.nextcloud.android.sso.model.SingleSignOnAccount;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.databinding.ActivityImportBinding;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.ui.exception.ExceptionHandler;

public class ImportAccountActivity extends AppCompatActivity implements AccountImporter.IAccountAccessGranted, Observer<ImportAccountUIState> {

    private ActivityImportBinding binding;
    private ImportAccountViewModel vm;
    public LiveData<ImportAccountUIState> state$;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setTheme(R.style.AppTheme);

        vm = new ViewModelProvider(this).get(ImportAccountViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_import);
        binding.setActivity(this);

        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            binding.image.setClipToOutline(true);
        }

        state$ = vm.getState$();
        state$.observe(this, this);
    }

    @Override
    public void onChanged(@NonNull ImportAccountUIState state) {
        final var account = state.account();
        final var error = state.error();

        if (error != null) {
            if (error instanceof SSOException ssoException) {
                UiExceptionManager.showDialogForException(this, ssoException);
            } else {
                ExceptionDialogFragment.newInstance(error, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }

        } else if (!state.importRunning() && account != null) {
            setResult(RESULT_OK);
            finish();
        }
    }

    public void accountSelectionStarted(@NonNull View addButton) {
        vm.accountSelectionStarted();

        try {
            AccountImporter.pickNewAccount(this);

        } catch (AndroidGetAccountsPermissionNotGranted e) {
            AccountImporter.requestAndroidAccountPermissionsAndPickAccount(this);

        } catch (NextcloudFilesAppNotInstalledException e) {
            UiExceptionManager.showDialogForException(this, e);
            vm.accountSelectionFinished(e);

        } catch (Throwable t) {
            vm.accountSelectionFinished(t);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_AUTH_TOKEN_SSO || resultCode != RESULT_CANCELED) {

            try {
                AccountImporter.onActivityResult(requestCode, resultCode, data, ImportAccountActivity.this, this);

            } catch (AccountImportCancelledException e) {
                vm.accountSelectionFinished(e);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_GET_ACCOUNTS_PERMISSION: {

                // SSO does not allow us to react to not granted permissions, but shows an exception dialog directly.
                // We therefore have to manually check the permissions in order to be able to let our ViewModel know about a failed import.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

                } else {
                    vm.accountSelectionFinished(new AndroidGetAccountsPermissionNotGranted(this));

                }
            }
        }
    }

    @Override
    public void accountAccessGranted(@NonNull SingleSignOnAccount account) {
        vm.accountSelectionFinished(new Account(account.url, account.name, account.userId));
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, ImportAccountActivity.class);
    }
}
