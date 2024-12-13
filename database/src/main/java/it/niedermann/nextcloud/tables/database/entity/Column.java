package it.niedermann.nextcloud.tables.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.Instant;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.attributes.DateTimeAttributes;
import it.niedermann.nextcloud.tables.database.entity.attributes.NumberAttributes;
import it.niedermann.nextcloud.tables.database.entity.attributes.SelectionAttributes;
import it.niedermann.nextcloud.tables.database.entity.attributes.TextAttributes;
import it.niedermann.nextcloud.tables.database.entity.attributes.UserGroupAttributes;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.Value;

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
                @Index(value = "orderWeight"),
        }
)
public class Column extends AbstractTableRelatedEntity {

    @ColumnInfo(defaultValue = "")
    private String title = "";

    @ColumnInfo(defaultValue = "")
    private String createdBy;

    private Instant createdAt;

    @ColumnInfo(defaultValue = "")
    private String lastEditBy;

    private Instant lastEditAt;

    @NonNull
    private EDataType dataType;

    private boolean mandatory;

    @ColumnInfo(defaultValue = "")
    private String description;

    private Integer orderWeight;

    @Embedded(prefix = "default_")
    @NonNull
    private Value defaultValue;

    @Embedded
    @NonNull
    private NumberAttributes numberAttributes;

    @Embedded
    @NonNull
    private DateTimeAttributes dateTimeAttributes;

    @Embedded
    @NonNull
    private SelectionAttributes selectionAttributes;

    @Embedded
    @NonNull
    private TextAttributes textAttributes;

    @Embedded
    @NonNull
    private UserGroupAttributes userGroupAttributes;

    public Column() {
        dataType = EDataType.UNKNOWN;
        defaultValue = new Value();
        numberAttributes = new NumberAttributes();
        dateTimeAttributes = new DateTimeAttributes();
        selectionAttributes = new SelectionAttributes();
        textAttributes = new TextAttributes();
        userGroupAttributes = new UserGroupAttributes();
    }

    @Ignore
    public Column(@NonNull Column column) {
        super(column);
        this.title = column.getTitle();
        this.createdBy = column.getCreatedBy();
        this.createdAt = column.getCreatedAt();
        this.lastEditBy = column.getLastEditBy();
        this.lastEditAt = column.getLastEditAt();
        this.dataType = column.getDataType();
        this.mandatory = column.isMandatory();
        this.description = column.getDescription();
        this.orderWeight = column.getOrderWeight();
        this.defaultValue = column.getDefaultValue();
        this.numberAttributes = column.getNumberAttributes();
        this.dateTimeAttributes = column.getDateTimeAttributes();
        this.selectionAttributes = column.getSelectionAttributes();
        this.textAttributes = column.getTextAttributes();
        this.userGroupAttributes = column.getUserGroupAttributes();
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

    @NonNull
    public EDataType getDataType() {
        return dataType;
    }

    public void setDataType(@NonNull EDataType dataType) {
        this.dataType = dataType;
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

    @NonNull
    public Value getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(@NonNull Value defaultValue) {
        this.defaultValue = defaultValue;
    }

    @NonNull
    public NumberAttributes getNumberAttributes() {
        return numberAttributes;
    }

    public void setNumberAttributes(@NonNull NumberAttributes numberAttributes) {
        this.numberAttributes = numberAttributes;
    }

    @NonNull
    public DateTimeAttributes getDateTimeAttributes() {
        return dateTimeAttributes;
    }

    public void setDateTimeAttributes(@NonNull DateTimeAttributes dateTimeAttributes) {
        this.dateTimeAttributes = dateTimeAttributes;
    }

    @NonNull
    public SelectionAttributes getSelectionAttributes() {
        return selectionAttributes;
    }

    public void setSelectionAttributes(@NonNull SelectionAttributes selectionAttributes) {
        this.selectionAttributes = selectionAttributes;
    }

    @NonNull
    public TextAttributes getTextAttributes() {
        return textAttributes;
    }

    public void setTextAttributes(@NonNull TextAttributes textAttributes) {
        this.textAttributes = textAttributes;
    }

    @NonNull
    public UserGroupAttributes getUserGroupAttributes() {
        return userGroupAttributes;
    }

    public void setUserGroupAttributes(@NonNull UserGroupAttributes userGroupAttributes) {
        this.userGroupAttributes = userGroupAttributes;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " (ID: " + id + ", Remote ID: " + remoteId + ", Status: " + status + ", Table ID: " + getTableId() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Column column = (Column) o;
        return mandatory == column.mandatory && Objects.equals(title, column.title) && Objects.equals(createdBy, column.createdBy) && Objects.equals(createdAt, column.createdAt) && Objects.equals(lastEditBy, column.lastEditBy) && Objects.equals(lastEditAt, column.lastEditAt) && dataType == column.dataType && Objects.equals(description, column.description) && Objects.equals(orderWeight, column.orderWeight) && Objects.equals(defaultValue, column.defaultValue) && Objects.equals(numberAttributes, column.numberAttributes) && Objects.equals(dateTimeAttributes, column.dateTimeAttributes) && Objects.equals(selectionAttributes, column.selectionAttributes) && Objects.equals(textAttributes, column.textAttributes) && Objects.equals(userGroupAttributes, column.userGroupAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, createdBy, createdAt, lastEditBy, lastEditAt, dataType, mandatory, description, orderWeight, defaultValue, numberAttributes, dateTimeAttributes, selectionAttributes, textAttributes, userGroupAttributes);
    }
}
