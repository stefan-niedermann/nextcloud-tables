package it.niedermann.nextcloud.tables.ui.accountswitcher;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.databinding.DialogAccountSwitcherBinding;
import it.niedermann.nextcloud.tables.ui.importaccount.ImportAccountActivity;
import it.niedermann.nextcloud.tables.ui.manageaccounts.ManageAccountsActivity;

public class AccountSwitcherDialog extends DialogFragment {

    private AccountSwitcherAdapter adapter;
    private DialogAccountSwitcherBinding binding;
    private AccountViewModel accountViewModel;

    private final ActivityResultLauncher<Intent> importAccountLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) {
            requireActivity().finish();
        }
    });

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAccountSwitcherBinding.inflate(requireActivity().getLayoutInflater());
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);

        adapter = new AccountSwitcherAdapter((localAccount -> {
            accountViewModel.setCurrentAccount(localAccount);
            dismiss();
        }));

        binding.accountLayout.setOnClickListener((v) -> dismiss());
        binding.check.setSelected(true);
        binding.accountsList.setAdapter(adapter);
        binding.addAccount.setOnClickListener((v) -> {
            importAccountLauncher.launch(ImportAccountActivity.createIntent(requireContext()));
            dismiss();
        });
        binding.manageAccounts.setOnClickListener((v) -> {
            requireActivity().startActivity(ManageAccountsActivity.createIntent(requireContext()));
            dismiss();
        });

        accountViewModel.getCurrentAccount().observe(this, account -> {
            binding.accountName.setText(
                    TextUtils.isEmpty(account.getDisplayName())
                            ? account.getUserName()
                            : account.getDisplayName()
            );
            binding.accountHost.setText(Uri.parse(account.getUrl()).getHost());

            Glide.with(requireContext())
                    .load(account.getAvatarUrl(DimensionUtil.INSTANCE.dpToPx(binding.currentAccountItemAvatar.getContext(), R.dimen.avatar_size)))
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .error(R.drawable.ic_baseline_account_circle_24)
                    .into(binding.currentAccountItemAvatar);
        });

        accountViewModel.getAccounts().observe(this, adapter::setAccounts);

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    public static DialogFragment newInstance() {
        return new AccountSwitcherDialog();
    }

}
