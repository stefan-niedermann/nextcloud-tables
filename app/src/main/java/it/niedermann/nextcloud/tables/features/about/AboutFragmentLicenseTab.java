package it.niedermann.nextcloud.tables.features.about;

import static it.niedermann.nextcloud.tables.util.SpannableUtil.setTextWithURL;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.databinding.FragmentAboutLicenseTabBinding;

public class AboutFragmentLicenseTab extends Fragment {

    private FragmentAboutLicenseTabBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_about_license_tab, container, false);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        setTextWithURL(binding.aboutIconsDisclaimerAppIcon, getResources(), R.string.about_icons_disclaimer_app_icon, R.string.about_app_icon_author_link_label, R.string.url_about_icon_author);
        setTextWithURL(binding.aboutIconsDisclaimerMdiIcons, getResources(), R.string.about_icons_disclaimer_mdi_icons, R.string.about_icons_disclaimer_mdi, R.string.url_about_icons_disclaimer_mdi);
        binding.aboutAppLicenseButton.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_license)))));

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    public static Fragment newInstance() {
        final var fragment = new AboutFragmentLicenseTab();

        final var args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

}