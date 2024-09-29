package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.model.SelectionDefault;

@Entity(
        inheritSuperIndices = true,
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Table.class,
                        parentColumns = "id",
                        childColumns = "tableId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(name = "IDX_COLUMN_ACCOUNT_ID_REMOTE_D", value = {"accountId", "remoteId"}, unique = true),
                @Index(name = "IDX_COLUMN_TABLE_ID", value = "tableId")
        }
)
public class Column extends AbstractRemoteEntity {
    private long tableId;
    @ColumnInfo(defaultValue = "")
    private String title = "";
    @ColumnInfo(defaultValue = "")
    private String createdBy;
    private Instant createdAt;
    @ColumnInfo(defaultValue = "")
    private String lastEditBy;
    private Instant lastEditAt;
    @ColumnInfo(defaultValue = "")
    private String type;
    @ColumnInfo(defaultValue = "")
    private String subtype;
    private boolean mandatory;
    @ColumnInfo(defaultValue = "")
    private String description;
    private Integer orderWeight;
    private Double numberDefault;
    private Double numberMin;
    private Double numberMax;
    private Integer numberDecimals;
    private String numberPrefix;
    private String numberSuffix;
    private String textDefault;
    private String textAllowedPattern;
    private Integer textMaxLength;
    @Ignore
    private List<SelectionOption> selectionOptions;
    private SelectionDefault selectionDefault;
    private String datetimeDefault;
    @Ignore
    private List<UserGroup> usergroupDefault;
    private boolean usergroupMultipleItems;
    private boolean usergroupSelectUsers;
    private boolean usergroupSelectGroups;
    private boolean showUserStatus;

    public Column() {
        // Default constructor
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastEditBy() {
        return lastEditBy;
    }

    public void setLastEditBy(String lastEditBy) {
        this.lastEditBy = lastEditBy;
    }

    public Instant getLastEditAt() {
        return lastEditAt;
    }

    public void setLastEditAt(Instant lastEditAt) {
        this.lastEditAt = lastEditAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderWeight() {
        return orderWeight;
    }

    public void setOrderWeight(Integer orderWeight) {
        this.orderWeight = orderWeight;
    }

    public Double getNumberDefault() {
        return numberDefault;
    }

    public void setNumberDefault(Double numberDefault) {
        this.numberDefault = numberDefault;
    }

    public Double getNumberMin() {
        return numberMin;
    }

    public void setNumberMin(Double numberMin) {
        this.numberMin = numberMin;
    }

    public Double getNumberMax() {
        return numberMax;
    }

    public void setNumberMax(Double numberMax) {
        this.numberMax = numberMax;
    }

    public Integer getNumberDecimals() {
        return numberDecimals;
    }

    public void setNumberDecimals(Integer numberDecimals) {
        this.numberDecimals = numberDecimals;
    }

    public String getNumberPrefix() {
        return numberPrefix;
    }

    public void setNumberPrefix(String numberPrefix) {
        this.numberPrefix = numberPrefix;
    }

    public String getNumberSuffix() {
        return numberSuffix;
    }

    public void setNumberSuffix(String numberSuffix) {
        this.numberSuffix = numberSuffix;
    }

    public String getTextDefault() {
        return textDefault;
    }

    public void setTextDefault(String textDefault) {
        this.textDefault = textDefault;
    }

    public String getTextAllowedPattern() {
        return textAllowedPattern;
    }

    public void setTextAllowedPattern(String textAllowedPattern) {
        this.textAllowedPattern = textAllowedPattern;
    }

    public Integer getTextMaxLength() {
        return textMaxLength;
    }

    public void setTextMaxLength(Integer textMaxLength) {
        this.textMaxLength = textMaxLength;
    }

    public List<SelectionOption> getSelectionOptions() {
        return selectionOptions;
    }

    public void setSelectionOptions(List<SelectionOption> selectionOptions) {
        this.selectionOptions = selectionOptions;
    }

    public SelectionDefault getSelectionDefault() {
        return selectionDefault;
    }

    public void setSelectionDefault(SelectionDefault selectionDefault) {
        this.selectionDefault = selectionDefault;
    }

    public String getDatetimeDefault() {
        return datetimeDefault;
    }

    public void setDatetimeDefault(String datetimeDefault) {
        this.datetimeDefault = datetimeDefault;
    }

    public List<UserGroup> getUsergroupDefault() {
        return usergroupDefault;
    }

    public void setUsergroupDefault(List<UserGroup> usergroupDefault) {
        this.usergroupDefault = usergroupDefault;
    }

    public boolean isUsergroupMultipleItems() {
        return usergroupMultipleItems;
    }

    public void setUsergroupMultipleItems(boolean usergroupMultipleItems) {
        this.usergroupMultipleItems = usergroupMultipleItems;
    }

    public boolean isUsergroupSelectUsers() {
        return usergroupSelectUsers;
    }

    public void setUsergroupSelectUsers(boolean usergroupSelectUsers) {
        this.usergroupSelectUsers = usergroupSelectUsers;
    }

    public boolean isUsergroupSelectGroups() {
        return usergroupSelectGroups;
    }

    public void setUsergroupSelectGroups(boolean usergroupSelectGroups) {
        this.usergroupSelectGroups = usergroupSelectGroups;
    }

    public boolean isShowUserStatus() {
        return showUserStatus;
    }

    public void setShowUserStatus(boolean showUserStatus) {
        this.showUserStatus = showUserStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Column column = (Column) o;
        return tableId == column.tableId && mandatory == column.mandatory && usergroupMultipleItems == column.usergroupMultipleItems && usergroupSelectUsers == column.usergroupSelectUsers && usergroupSelectGroups == column.usergroupSelectGroups && showUserStatus == column.showUserStatus && Objects.equals(title, column.title) && Objects.equals(createdBy, column.createdBy) && Objects.equals(createdAt, column.createdAt) && Objects.equals(lastEditBy, column.lastEditBy) && Objects.equals(lastEditAt, column.lastEditAt) && Objects.equals(type, column.type) && Objects.equals(subtype, column.subtype) && Objects.equals(description, column.description) && Objects.equals(orderWeight, column.orderWeight) && Objects.equals(numberDefault, column.numberDefault) && Objects.equals(numberMin, column.numberMin) && Objects.equals(numberMax, column.numberMax) && Objects.equals(numberDecimals, column.numberDecimals) && Objects.equals(numberPrefix, column.numberPrefix) && Objects.equals(numberSuffix, column.numberSuffix) && Objects.equals(textDefault, column.textDefault) && Objects.equals(textAllowedPattern, column.textAllowedPattern) && Objects.equals(textMaxLength, column.textMaxLength) && Objects.equals(selectionOptions, column.selectionOptions) && Objects.equals(selectionDefault, column.selectionDefault) && Objects.equals(datetimeDefault, column.datetimeDefault) && Objects.equals(usergroupDefault, column.usergroupDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tableId, title, createdBy, createdAt, lastEditBy, lastEditAt, type, subtype, mandatory, description, orderWeight, numberDefault, numberMin, numberMax, numberDecimals, numberPrefix, numberSuffix, textDefault, textAllowedPattern, textMaxLength, selectionOptions, selectionDefault, datetimeDefault, usergroupDefault, usergroupMultipleItems, usergroupSelectUsers, usergroupSelectGroups, showUserStatus);
    }
}
