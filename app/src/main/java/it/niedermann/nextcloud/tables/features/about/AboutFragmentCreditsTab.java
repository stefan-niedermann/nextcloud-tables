package it.niedermann.nextcloud.tables.features.about;

import static it.niedermann.nextcloud.tables.util.SpannableUtil.setTextWithURL;
import static it.niedermann.nextcloud.tables.util.SpannableUtil.url;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.model.TablesVersion;
import it.niedermann.nextcloud.tables.databinding.FragmentAboutCreditsTabBinding;
import it.niedermann.nextcloud.tables.databinding.ItemAccountAndVersionBinding;

public class AboutFragmentCreditsTab extends Fragment {

    private static final Logger logger = Logger.getLogger(AboutFragmentCreditsTab.class.getSimpleName());

    private FragmentAboutCreditsTabBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final var viewModel = new ViewModelProvider(requireActivity()).get(AboutViewModel.class);
        final var adapter = new ServerVersionsAdapter();
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_about_credits_tab, container, false);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);

        binding.aboutMaintainer.setText(url(getString(R.string.about_maintainer), getString(R.string.url_maintainer)));
        binding.aboutMaintainer.setMovementMethod(new LinkMovementMethod());
        binding.serverAppVersions.setAdapter(adapter);

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

    private static class ServerVersionsAdapter extends RecyclerView.Adapter<ServerVersionsViewHolder> {

        @NonNull
        private final List<Pair<String, TablesVersion>> data = new ArrayList<>(1);

        @NonNull
        @Override
        public ServerVersionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final var binding = ItemAccountAndVersionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ServerVersionsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ServerVersionsViewHolder holder, int position) {
            if (position >= data.size()) {
                holder.bindPending();
            } else {
                holder.bind(data.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(@Nullable Collection<Pair<String, TablesVersion>> data) {
            this.data.clear();

            if (data != null) {
                this.data.addAll(data);
            }

            this.notifyDataSetChanged();
        }
    }

    private static class ServerVersionsViewHolder extends RecyclerView.ViewHolder {

        private final ItemAccountAndVersionBinding binding;

        public ServerVersionsViewHolder(@NonNull ItemAccountAndVersionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            bindPending();
        }

        public void bind(@NonNull Pair<String, TablesVersion> entry) {
            binding.accountName.setText(entry.first);
            binding.serverVersion.setText(entry.second.toString());
        }

        public void bindPending() {
            binding.accountName.setText(null);
            binding.serverVersion.setText(null);
        }
    }

    @BindingAdapter({"app:serverVersions"})
    public static void setServerVersions(@NonNull RecyclerView view, @Nullable Collection<Pair<String, TablesVersion>> data) {
        if (view.getAdapter() instanceof ServerVersionsAdapter adapter) {
            adapter.setData(data);
        } else {
            logger.warning(() -> "Can not bind data, expected " + RecyclerView.class.getSimpleName() + " to be of type " + ServerVersionsAdapter.class.getSimpleName() + " but was " + view.getClass().getSimpleName());
        }
    }

    @BindingAdapter({"app:appVersion"})
    public static void setAppVersion(@NonNull TextView view, @Nullable String appVersion) {
        view.setText(Optional.ofNullable(appVersion).map(v -> view.getContext().getString(R.string.about_version, v)).orElse(null));
    }
}