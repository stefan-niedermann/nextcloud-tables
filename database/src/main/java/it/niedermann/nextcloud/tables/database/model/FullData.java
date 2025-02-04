package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.database.entity.DataSelectionOptionCrossRef;
import it.niedermann.nextcloud.tables.database.entity.DataUserGroupCrossRef;
import it.niedermann.nextcloud.tables.database.entity.LinkValue;
import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.entity.UserGroup;

public class FullData implements Serializable {

    @NonNull
    @Embedded
    private Data data;

    @NonNull
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = DataSelectionOptionCrossRef.class,
                    parentColumn = "dataId",
                    entityColumn = "selectionOptionId"
            )
    )
    private List<SelectionOption> selectionOptions;

    @NonNull
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = DataUserGroupCrossRef.class,
                    parentColumn = "dataId",
                    entityColumn = "userGroupId"
            )
    )
    private List<UserGroup> userGroups;

    @NonNull
    @Relation(
            parentColumn = "columnId",
            entity = Column.class,
            entityColumn = "id",
            projection = "dataType"
    )
    private EDataType dataType;


    @Nullable
    @Relation(
            entity = LinkValue.class,
            parentColumn = "linkValueRef",
            entityColumn = "dataId"
    )
    private LinkValueWithProviderId linkValueWithProviderRemoteId;

    public FullData() {
        this(new Data());
    }

    @Ignore
    public FullData(@NonNull EDataType dataType) {
        this(new Data(), dataType);
    }

    @Ignore
    public FullData(@NonNull Data data) {
        this(data, EDataType.UNKNOWN);
    }

    @Ignore
    protected FullData(@NonNull Data data,
                       @NonNull EDataType dataType) {
        this.data = data;
        this.selectionOptions = Collections.emptyList();
        this.userGroups = Collections.emptyList();
        this.dataType = dataType;
    }

    @Ignore
    public FullData(@NonNull FullData fullData) {
        this.data = new Data(fullData.getData());
        this.selectionOptions = fullData.getSelectionOptions().stream().map(SelectionOption::new).collect(Collectors.toUnmodifiableList());
        this.userGroups = fullData.getUserGroups().stream().map(UserGroup::new).collect(Collectors.toUnmodifiableList());
        this.dataType = fullData.getDataType();
        this.linkValueWithProviderRemoteId = Optional.ofNullable(fullData.getLinkValueWithProviderRemoteId()).map(LinkValueWithProviderId::new).orElse(null);
    }

    @NonNull
    public Data getData() {
        return data;
    }

    public void setData(@NonNull Data data) {
        this.data = data;
    }

    @NonNull
    public List<SelectionOption> getSelectionOptions() {
        return selectionOptions;
    }

    public void setSelectionOptions(@NonNull List<SelectionOption> selectionOptions) {
        this.selectionOptions = selectionOptions;
    }

    @NonNull
    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(@NonNull List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    @NonNull
    public EDataType getDataType() {
        return dataType;
    }

    public void setDataType(@NonNull EDataType dataType) {
        this.dataType = dataType;
    }

    @Nullable
    public LinkValueWithProviderId getLinkValueWithProviderRemoteId() {
        return linkValueWithProviderRemoteId;
    }

    public void setLinkValueWithProviderRemoteId(@Nullable LinkValueWithProviderId linkValueWithProviderRemoteId) {
        this.linkValueWithProviderRemoteId = linkValueWithProviderRemoteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullData fullData = (FullData) o;
        return Objects.equals(data, fullData.data) && Objects.equals(selectionOptions, fullData.selectionOptions) && Objects.equals(userGroups, fullData.userGroups) && dataType == fullData.dataType && Objects.equals(linkValueWithProviderRemoteId, fullData.linkValueWithProviderRemoteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, selectionOptions, userGroups, dataType, linkValueWithProviderRemoteId);
    }
}
