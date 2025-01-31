package it.niedermann.nextcloud.tables.features.row.editor.type;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.databinding.EditAutocompleteBinding;
import it.niedermann.nextcloud.tables.features.row.editor.OnTextChangedListener;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;

public abstract class AutocompleteEditView<ProposalProviderType> extends DataEditView<EditAutocompleteBinding> {

    protected ProposalArrayAdapter adapter;
    protected final ProposalProvider<ProposalProviderType> proposalProvider;
    protected final Account account;
    private final CircularProgressDrawable pendingProposals;

    public AutocompleteEditView(@NonNull Context context) {
        super(context, EditAutocompleteBinding.inflate(LayoutInflater.from(context)));
        proposalProvider = null;
        account = null;
        pendingProposals = null;
    }

    public AutocompleteEditView(@NonNull Context context,
                                @Nullable AttributeSet attrs) {
        super(context, attrs, EditAutocompleteBinding.inflate(LayoutInflater.from(context)));
        proposalProvider = null;
        account = null;
        pendingProposals = null;
    }

    protected AutocompleteEditView(@NonNull Account account,
                                   @NonNull Context context,
                                   @NonNull Column column,
                                   @NonNull ProposalProvider<ProposalProviderType> proposalProvider,
                                   @Nullable FragmentManager fragmentManager) {
        this(account, context, column, proposalProvider, fragmentManager, null);
    }

    protected AutocompleteEditView(@NonNull Account account,
                                   @NonNull Context context,
                                   @NonNull Column column,
                                   @NonNull ProposalProvider<ProposalProviderType> proposalProvider,
                                   @Nullable FragmentManager fragmentManager,
                                   @DrawableRes @Nullable Integer iconStart) {
        super(context, EditAutocompleteBinding.inflate(LayoutInflater.from(context)), column, fragmentManager);
        this.proposalProvider = proposalProvider;
        this.account = account;
        this.pendingProposals = new CircularProgressDrawable(context);

        if (iconStart == null) {
            this.binding.searchWrapper.setStartIconDrawable(null);
        } else {
            this.binding.searchWrapper.setStartIconDrawable(iconStart);
        }

        final var term$ = new ReactiveLiveData<String>();

        term$
                .observe(this, term -> {
                    final var termIsEmpty = Optional.ofNullable(term).map(String::isEmpty).orElse(true);
                    if (termIsEmpty) {
                        writeSelectedValueToModel(null);
                    }
                });

        term$
                // TODO Check debouncing
                // .debounce(300, ChronoUnit.MILLIS)
                .filter(Objects::nonNull)
                .tap(() -> binding.searchWrapper.setEndIconDrawable(pendingProposals))
                .flatMap(term -> proposalProvider.getProposals(account, column, term))
                .tap(() -> binding.searchWrapper.setEndIconDrawable(pendingProposals))
                .observe(this, proposals -> Optional
                        .ofNullable(adapter)
                        .ifPresent(adapter -> adapter.setData(proposals)));

        binding.search.addTextChangedListener((OnTextChangedListener) (s, start, before, count) -> term$.postValue(s.toString()));
        binding.searchWrapper.setEndIconOnClickListener(v -> binding.search.setText(null));
    }

    public void setAdapter(@NonNull ProposalArrayAdapter adapter) {
        this.adapter = adapter;
        binding.search.setAdapter(adapter);
    }

    @Override
    public void setErrorMessage(@Nullable String message) {
        binding.searchWrapper.setError(message);
    }

    @Override
    public void setFullData(@NonNull FullData fullData) {
        super.setFullData(fullData);

        Optional.ofNullable(fullDataToDropDownString())
                .ifPresentOrElse(binding.search::setText, () -> binding.search.setText(null));
    }

    protected abstract void writeSelectedValueToModel(@Nullable ProposalProviderType proposal);

    @Nullable
    protected abstract String fullDataToDropDownString();

    public abstract class ProposalArrayAdapter extends ArrayAdapter<ProposalProviderType> {

        private final int paddingVertical;
        private final int paddingHorizontal;

        public ProposalArrayAdapter(@NonNull Context context) {
            super(context, 0, 0);
            final var resources = context.getResources();
            this.paddingVertical = resources.getDimensionPixelSize(it.niedermann.nextcloud.tables.ui.R.dimen.spacer_1x);
            this.paddingHorizontal = resources.getDimensionPixelSize(it.niedermann.nextcloud.tables.ui.R.dimen.spacer_2x);
        }

        public void setData(@NonNull Collection<ProposalProviderType> proposals) {
            clear();
            addAll(proposals);
            notifyDataSetChanged();
            binding.search.showDropDown();
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @NonNull
        @Override
        public final View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final var item = getItem(position);
            final var view = getView(item, convertView, parent);

            view.setPaddingRelative(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            view.setOnClickListener(proposal -> {
                binding.search.dismissDropDown();
                final var selectedItem = getItem(position);
                writeSelectedValueToModel(selectedItem);
                Optional.ofNullable(fullDataToDropDownString())
                        .ifPresentOrElse(binding.search::setText, () -> binding.search.setText(null));
                onValueChanged();
            });

            return view;
        }

        /// @return a NoOp implementation of [Filter]. [Filter#performFiltering] will
        /// always return `null`, [Filter#publishResults] does nothing.
        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                }
            };
        }

        @NonNull
        public abstract View getView(@Nullable ProposalProviderType item,
                                     @Nullable View convertView,
                                     @NonNull ViewGroup parent);
    }
}