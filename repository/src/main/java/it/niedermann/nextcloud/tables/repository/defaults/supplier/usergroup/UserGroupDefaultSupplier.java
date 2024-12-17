package it.niedermann.nextcloud.tables.repository.defaults.supplier.usergroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.UserGroup;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class UserGroupDefaultSupplier extends DefaultValueSupplier {

    @Override
    protected void applyDefaultValue(@NonNull FullColumn fullColumn, @NonNull FullData fullData) {
        final var userGroups = fullData.getUserGroups();

        if (userGroups == null || userGroups.isEmpty()) {

            final var defaultUserGroups = fullColumn.getDefaultUserGroups();
            final var multiple = fullColumn.getColumn().getUserGroupAttributes().usergroupMultipleItems();

            if (multiple) {
                Optional.ofNullable(defaultUserGroups)
                        .ifPresent(fullData::setUserGroups);

            } else {
                getSingle(defaultUserGroups)
                        .map(Collections::singletonList)
                        .ifPresent(fullData::setUserGroups);
            }
        }
    }

    private Optional<UserGroup> getSingle(@Nullable Collection<UserGroup> userGroups) {
        if (userGroups == null || userGroups.isEmpty()) {
            return Optional.empty();
        }
        return userGroups.stream().findAny();
    }
}
