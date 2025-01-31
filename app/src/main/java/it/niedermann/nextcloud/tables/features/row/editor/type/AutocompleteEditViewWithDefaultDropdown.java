package it.niedermann.nextcloud.tables.features.row.editor.type;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.databinding.ItemAutocompleteBinding;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;
import it.niedermann.nextcloud.tables.util.AvatarUtil;

public abstract class AutocompleteEditViewWithDefaultDropdown<ProposalProviderType> extends AutocompleteEditView<ProposalProviderType> {

    protected final AvatarUtil avatarUtil;
    @DrawableRes
    @Nullable
    Integer semanticImage;

    public AutocompleteEditViewWithDefaultDropdown(@NonNull Context context) {
        super(context);
        avatarUtil = null;
    }

    public AutocompleteEditViewWithDefaultDropdown(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        avatarUtil = null;
    }

    public AutocompleteEditViewWithDefaultDropdown(@NonNull Account account,
                                                   @NonNull Context context,
                                                   @NonNull Column column,
                                                   @NonNull ProposalProvider<ProposalProviderType> proposalProvider,
                                                   @DrawableRes int drawable) {
        super(account, context, column, proposalProvider, null);
        setAdapter(new AutocompleteArrayAdapter(context));
        avatarUtil = new AvatarUtil();
        this.semanticImage = drawable;
    }

    abstract protected Optional<String> getTitle(@Nullable ProposalProviderType item);

    abstract protected Optional<String> getSubline(@Nullable ProposalProviderType item);

    abstract protected Optional<ThumbDescriptor> getThumb(@Nullable ProposalProviderType item, @Px int size);

    protected class AutocompleteArrayAdapter extends ProposalArrayAdapter {

        public AutocompleteArrayAdapter(@NonNull Context context) {
            super(context);
        }

        @NonNull
        @Override
        public View getView(@Nullable ProposalProviderType item, @Nullable View convertView, @NonNull ViewGroup parent) {
            final var binding = convertView == null
                    ? ItemAutocompleteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
                    : ItemAutocompleteBinding.bind(convertView);

            final var title = getTitle(item);
            final var subline = getSubline(item);
            final var thumb = getThumb(item, binding.thumb.getWidth());

            binding.title.setText(title.orElse(null));
            binding.subline.setText(subline.orElse(null));
            binding.subline.setVisibility(subline.map(v -> View.VISIBLE).orElse(View.GONE));
            binding.thumb.setVisibility(subline.map(v -> View.VISIBLE).orElse(View.GONE));
            thumb.ifPresent(thumbDescriptor -> {
                final var requestBuilder = Glide
                        .with(binding.thumb)
                        .load(thumbDescriptor.url());

                Optional
                        .ofNullable(thumbDescriptor.placeholder)
                        .or(() -> Optional.ofNullable(semanticImage))
                        .map(requestBuilder::placeholder)
                        .orElse(requestBuilder)
                        .error(thumbDescriptor.error)
                        .into(binding.thumb);
            });
            return binding.getRoot();
        }
    }

    public record ThumbDescriptor(
            @NonNull GlideUrl url,
            @Nullable @DrawableRes Integer placeholder,
            @Nullable @DrawableRes Integer error
    ) {

    }
}
