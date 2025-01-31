package it.niedermann.nextcloud.tables.features.row.editor.type.usergroup;

import static java.util.function.Predicate.not;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.bumptech.glide.load.model.GlideUrl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Account;
import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.EUserGroupType;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.features.row.editor.ProposalProvider;
import it.niedermann.nextcloud.tables.features.row.editor.type.AutocompleteEditViewWithDefaultDropdown;
import it.niedermann.nextcloud.tables.remote.ocs.model.OcsAutocompleteResult;

public class UserGroupEditor extends AutocompleteEditViewWithDefaultDropdown<OcsAutocompleteResult> {

    public UserGroupEditor(@NonNull Context context) {
        super(context);
    }

    public UserGroupEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UserGroupEditor(@NonNull Account account,
                           @NonNull Context context,
                           @NonNull Column column,
                           @NonNull ProposalProvider<OcsAutocompleteResult> proposalProvider) {
        super(account, context, column, proposalProvider, R.drawable.ic_baseline_person_24);
    }

    @Override
    protected void writeSelectedValueToModel(@Nullable OcsAutocompleteResult proposal) {

        assert fullData != null;

        Optional.ofNullable(proposal).map(OcsAutocompleteResult::source);

        final var userGroups = Optional.ofNullable(proposal)
                .filter(Objects::nonNull)
                .map(this::mapAutocompleteResultToUserGroup)
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);

        fullData.setUserGroups(userGroups);
    }

    @NonNull
    @Override
    public Optional<String> validate() {
        final var errorMessages = super.validate();

        if (errorMessages.isPresent()) {
            return errorMessages;
        }

        final boolean valid = !column.isMandatory() || Optional
                .ofNullable(fullData)
                .map(FullData::getUserGroups)
                .map(usergroups -> !usergroups.isEmpty())
                .orElse(false);

        return valid
                ? Optional.empty()
                : Optional.of(getContext().getString(R.string.validation_mandatory));
    }

    @NonNull
    private UserGroup mapAutocompleteResultToUserGroup(@NonNull OcsAutocompleteResult proposal) {
        final var userGroup = new UserGroup();
        userGroup.setAccountId(account.getId());
        userGroup.setKey(proposal.id());
        userGroup.setRemoteId(proposal.id());
        userGroup.setType(mapAutocompleteSourceToUserGroupType(proposal.source()));
        return userGroup;
    }

    @NonNull
    private EUserGroupType mapAutocompleteSourceToUserGroupType(@Nullable OcsAutocompleteResult.OcsAutocompleteSource source) {
        if (source == null) {
            return EUserGroupType.UNKNOWN;
        }

        return EUserGroupType.findByRemoteId(source.shareType);
    }

    @Nullable
    @Override
    protected String fullDataToDropDownString() {
        return Optional.ofNullable(fullData)
                .map(FullData::getUserGroups)
                .filter(not(List::isEmpty))
                .map(List::stream)
                .map(userGroups -> userGroups
                        .map(UserGroup::getRemoteId)
                        .collect(Collectors.joining(", @", "@", "")))
                .orElse(null);
    }

    @Override
    protected Optional<String> getTitle(@Nullable OcsAutocompleteResult item) {
        final var optionalItem = Optional.ofNullable(item);

//        final var subline = optionalItem
//                .map(OcsAutocompleteResult::subline)
//                .filter(not(String::isBlank));

        return optionalItem
                .map(OcsAutocompleteResult::label)
                .filter(not(String::isBlank))
                .or(() -> optionalItem
                        .map(OcsAutocompleteResult::id)
                        .filter(not(String::isBlank)));
    }

    @Override
    protected Optional<String> getSubline(@Nullable OcsAutocompleteResult item) {
        return Optional.ofNullable(item)
                .map(OcsAutocompleteResult::subline)
                .filter(not(String::isBlank));
    }

    @Override
    protected Optional<ThumbDescriptor> getThumb(@Nullable OcsAutocompleteResult item, @Px int size) {
        final var optionalItem = Optional.ofNullable(item);
        return optionalItem.map(OcsAutocompleteResult::icon)
                .filter(not(TextUtils::isEmpty))
                .map(GlideUrl::new)
                .or(() -> optionalItem
                        .map(OcsAutocompleteResult::id)
                        .filter(not(String::isBlank))
                        .map(userGroupId -> avatarUtil.getAvatarUrl(account, size, userGroupId)))
                .map(url -> new ThumbDescriptor(url,
                        R.drawable.ic_baseline_person_24,
                        it.niedermann.android.markdown.R.drawable.ic_baseline_broken_image_24));

    }
}
