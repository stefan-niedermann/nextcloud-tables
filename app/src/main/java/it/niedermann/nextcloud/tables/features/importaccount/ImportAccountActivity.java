package it.niedermann.nextcloud.tables.features.importaccount;

import static com.nextcloud.android.sso.AccountImporter.REQUEST_AUTH_TOKEN_SSO;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
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
import com.nextcloud.android.sso.model.SingleSignOnAccount;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.databinding.ActivityImportBinding;
import it.niedermann.nextcloud.tables.features.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.tables.features.exception.ExceptionHandler;
import it.niedermann.nextcloud.tables.repository.SyncWorker;
import it.niedermann.nextcloud.tables.repository.exception.AccountAlreadyImportedException;
import it.niedermann.nextcloud.tables.repository.exception.ServerNotAvailableException;
import it.niedermann.nextcloud.tables.repository.sync.report.SyncStatus;
import it.niedermann.nextcloud.tables.util.AvatarUtil;

public class ImportAccountActivity extends AppCompatActivity implements AccountImporter.IAccountAccessGranted {

    private static final Logger logger = Logger.getLogger(ImportAccountActivity.class.getSimpleName());

    private final AvatarUtil avatarUtil = new AvatarUtil();
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

        final var finishAllOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };
        getOnBackPressedDispatcher().addCallback(finishAllOnBackPressedCallback);
        importAccountViewModel.noAccountExists().observe(this, finishAllOnBackPressedCallback::setEnabled);

        binding.addButton.setOnClickListener(this::onAddButtonClicked);
    }

    private void onAddButtonClicked(@NonNull View addButton) {
        binding.addButton.setEnabled(false);

        try {
            AccountImporter.pickNewAccount(this);

        } catch (AndroidGetAccountsPermissionNotGranted e) {
            AccountImporter.requestAndroidAccountPermissionsAndPickAccount(this);

        } catch (NextcloudFilesAppNotInstalledException e) {
            UiExceptionManager.showDialogForException(this, e);
            binding.addButton.setEnabled(true);

        } catch (Throwable t) {
            binding.addButton.setEnabled(true);
        }
    }

    @Override
    public void accountAccessGranted(@NonNull SingleSignOnAccount account) {
        importAccountViewModel
                .createAccount(new Account(account.url, account.name, account.userId))
                .observe(this, this::applyImportState);
    }

    private void applyImportState(@NonNull SyncStatus syncStatus) {

        switch (syncStatus.getStep()) {
            case START -> {
                setAvatar(syncStatus.getAccount());
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.progressText.setVisibility(View.VISIBLE);
                binding.progressText.setText(R.string.import_state_import_account);
                binding.addButton.setEnabled(false);

                binding.progressCircular.setIndeterminate(true);
            }
            case PROGRESS -> {
                setAvatar(syncStatus.getAccount());
                binding.progressCircular.setVisibility(View.VISIBLE);
                binding.progressText.setVisibility(View.VISIBLE);
                binding.progressText.setText(R.string.import_state_import_tables);
                binding.addButton.setEnabled(false);

                binding.progressCircular.setIndeterminate(false);
                binding.progressCircular.setMax(syncStatus.getTablesTotalCount().orElse(100));
                binding.progressCircular.setProgress(syncStatus.getTablesFinishedCount().orElse(0), true);
                binding.progressCircular.setSecondaryProgress(syncStatus.getTablesFinishedCount().orElse(0) + syncStatus.getTablesInProgress().size());
            }
            case FINISHED -> {
                setAvatar(syncStatus.getAccount());
                binding.progressCircular.setVisibility(View.GONE);
                binding.progressText.setVisibility(View.GONE);

                binding.progressCircular.setIndeterminate(false);
                binding.progressCircular.setMax(1);
                binding.progressCircular.setProgress(1, true);
                binding.progressCircular.setSecondaryProgress(1);

                if (syncStatus.getError() == null) {
                    SyncWorker.update(getApplicationContext());
                    setResult(RESULT_OK);
                    finish();
                }
            }
            case ERROR -> {
                binding.image.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));
                binding.progressCircular.setVisibility(View.GONE);
                binding.progressText.setVisibility(View.VISIBLE);
                binding.addButton.setEnabled(true);

                binding.progressCircular.setIndeterminate(false);
                binding.progressCircular.setMax(syncStatus.getTablesTotalCount().orElse(100));
                binding.progressCircular.setProgress(syncStatus.getTablesFinishedCount().orElse(0), true);
                binding.progressCircular.setSecondaryProgress(syncStatus.getTablesFinishedCount().orElse(0) + syncStatus.getTablesInProgress().size());

                final var error = syncStatus.getError();
                if (error instanceof AccountAlreadyImportedException) {
                    binding.progressText.setText(R.string.account_already_imported);
                } else {
                    if (error != null) {
                        logger.log(Level.SEVERE, error.toString(), error);
                        ExceptionDialogFragment.newInstance(error, syncStatus.getAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());

                        if (error instanceof ServerNotAvailableException) {
                            binding.progressText.setText(((ServerNotAvailableException) error).getReason().messageRes);
                        } else {
                            binding.progressText.setText(error.getMessage());
                        }
                    } else {
                        binding.progressText.setText(R.string.hint_error_appeared);
                        logger.log(Level.SEVERE, "Received error step while importing, but exception was null", new IllegalStateException());
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + syncStatus.getStep());
        }
    }

    private void setAddButtonEnabled(@NonNull SyncStatus syncStatus) {
        binding.addButton.setEnabled(syncStatus.isFinished());
    }

    private void setAvatar(@Nullable Account account) {
        if (account == null) {
            throw new NullPointerException();
        }

        binding.progressText.setText(getString(R.string.importing_account, account.getDisplayName()));
        Glide.with(binding.image)
                .load(avatarUtil.getAvatarUrl(account, binding.image.getWidth()))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(binding.image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_AUTH_TOKEN_SSO || resultCode != RESULT_CANCELED) {
            try {
                AccountImporter.onActivityResult(requestCode, resultCode, data, ImportAccountActivity.this, this);
            } catch (AccountImportCancelledException e) {
                logger.info("Account import has been canceled.");
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
