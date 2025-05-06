package it.niedermann.nextcloud.tables.features.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.databinding.FragmentAboutContributionTabBinding;

public class AboutFragmentContributingTab extends Fragment {

    private FragmentAboutContributionTabBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_about_contribution_tab, container, false);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.aboutSource.setText(getString(R.string.about_source, getString(R.string.url_source)));
        binding.aboutIssues.setText(getString(R.string.about_issues, getString(R.string.url_issues)));
        binding.aboutTranslate.setText(getString(R.string.about_translate, getString(R.string.url_translations)));
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}