package it.niedermann.nextcloud.tables.features.about;

import static it.niedermann.nextcloud.tables.util.SpannableUtil.setTextWithURL;
import static it.niedermann.nextcloud.tables.util.SpannableUtil.url;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.databinding.FragmentAboutCreditsTabBinding;
import it.niedermann.nextcloud.tables.repository.PreferencesRepository;

public class AboutFragmentCreditsTab extends Fragment {

    private static final String BUNDLE_KEY_ACCOUNT = "account";

    private FragmentAboutCreditsTabBinding binding;
    private PreferencesRepository preferencesRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutCreditsTabBinding.inflate(inflater, container, false);
        preferencesRepository = new PreferencesRepository(requireContext());

        // VERSIONS

        binding.aboutVersion.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));
        final var args = getArguments();
        if (args != null && args.containsKey(BUNDLE_KEY_ACCOUNT)) {
            final var account = (Account) requireArguments().getSerializable(BUNDLE_KEY_ACCOUNT);
            binding.aboutServerAppVersion.setText(account == null ? getString(R.string.simple_exception) : account.getTablesVersion().toString());
        } else {
            binding.aboutServerAppVersionContainer.setVisibility(View.GONE);
        }

        preferencesRepository.getLastBackgroundSync$().observe(getViewLifecycleOwner(), lastBackgroundSync -> {
            if (lastBackgroundSync == null) {
                binding.lastBackgroundSync.setText(R.string.simple_disabled);
            } else {
                binding.lastBackgroundSync.setText(DateUtils.getRelativeTimeSpanString(lastBackgroundSync.toEpochMilli()));
            }
        });

        binding.aboutMaintainer.setText(url(getString(R.string.about_maintainer), getString(R.string.url_maintainer)));
        binding.aboutMaintainer.setMovementMethod(new LinkMovementMethod());
        setTextWithURL(binding.aboutTranslators, getResources(), R.string.about_translators_transifex, R.string.about_translators_transifex_label, R.string.url_translations);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    public static Fragment newInstance() {
        return new AboutFragmentCreditsTab();
    }

    public static Fragment newInstance(@Nullable Account account) {
        if (account == null) {
            return newInstance();
        }
        final Fragment fragment = new AboutFragmentCreditsTab();
        final Bundle args = new Bundle();
        args.putSerializable(BUNDLE_KEY_ACCOUNT, account);
        fragment.setArguments(args);
        return fragment;
    }
}