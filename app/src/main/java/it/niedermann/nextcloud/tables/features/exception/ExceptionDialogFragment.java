package it.niedermann.nextcloud.tables.features.exception;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.exception.ExceptionUtil;
import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.databinding.DialogExceptionBinding;
import it.niedermann.nextcloud.tables.features.exception.tips.TipsAdapter;

public class ExceptionDialogFragment extends AppCompatDialogFragment {

    private static final Logger logger = Logger.getLogger(ExceptionDialogFragment.class.getSimpleName());

    private static final String KEY_THROWABLE = "throwable";
    private static final String KEY_ACCOUNT = "account";
    public static final String INTENT_EXTRA_BUTTON_TEXT = "button_text";

    private Throwable throwable;

    @Nullable
    private Account account;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        final var args = getArguments();
        if (args != null) {
            this.throwable = (Throwable) args.getSerializable(KEY_THROWABLE);
            if (this.throwable == null) {
                throwable = new IllegalArgumentException("Did not receive any exception in " + ExceptionDialogFragment.class.getSimpleName());
            }
            this.account = (Account) args.getSerializable(KEY_ACCOUNT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final var view = View.inflate(getContext(), R.layout.dialog_exception, null);
        final var binding = DialogExceptionBinding.bind(view);

        final var adapter = new TipsAdapter(actionIntent -> requireActivity().startActivity(actionIntent));

        final String debugInfos = ExceptionUtil.getDebugInfos(requireContext(), throwable, BuildConfig.FLAVOR, account == null ? null : String.valueOf(account.getTablesVersion()));

        binding.tips.setAdapter(adapter);
        binding.stacktrace.setText(debugInfos);

        logger.log(Level.SEVERE, throwable.toString(), throwable);

        adapter.setThrowable(requireContext(), account, throwable);

        return new MaterialAlertDialogBuilder(requireActivity())
                .setView(binding.getRoot())
                .setTitle(R.string.error_dialog_title)
                .setPositiveButton(android.R.string.copy, (a, b) -> {
                    ClipboardUtil.copyToClipboard(requireContext(), getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                    a.dismiss();
                })
                .setNegativeButton(R.string.simple_close, null)
                .create();
    }

    public static DialogFragment newInstance(@NonNull Throwable throwable, @Nullable Account account) {
        final var fragment = new ExceptionDialogFragment();
        final var args = new Bundle();
        args.putSerializable(KEY_THROWABLE, throwable);
        args.putSerializable(KEY_ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }

}
