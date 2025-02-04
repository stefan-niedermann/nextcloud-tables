package it.niedermann.nextcloud.tables.features.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.databinding.ActivityAboutBinding;
import it.niedermann.nextcloud.tables.features.exception.ExceptionHandler;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding;
    private final static int[] tabTitles = new int[]{
            R.string.about_credits_tab_title,
            R.string.about_contribution_tab_title,
            R.string.about_license_tab_title
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollView, (v, windowInsets) -> {
            final var insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            final var mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        binding.viewPager.setAdapter(new TabsPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    private static class TabsPagerAdapter extends FragmentStateAdapter {

        TabsPagerAdapter(final FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return switch (position) {
                case 0 -> AboutFragmentCreditsTab.newInstance();
                case 1 -> new AboutFragmentContributingTab();
                case 2 -> AboutFragmentLicenseTab.newInstance();
                default -> throw new IllegalArgumentException("position must be between 0 and 2");
            };
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AboutActivity.class);
    }
}