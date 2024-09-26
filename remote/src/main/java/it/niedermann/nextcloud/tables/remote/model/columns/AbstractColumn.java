package it.niedermann.nextcloud.tables.remote.model.columns;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.remote.model.ENodeType;

public class AbstractColumn {

    private final long baseNodeId;
    private final String title;
    private final String description;
    private final String subType;
    private final boolean mandatory;
    private final ENodeType nodeType;
    private final Collection<Long> selectedViewIds;

    protected AbstractColumn(long tableRemoteId, @NonNull Column column) {
        this(
                tableRemoteId,
                column.getTitle(),
                column.getDescription(),
                column.getSubtype(),
                column.isMandatory(),
                ENodeType.TABLE,
                Collections.emptySet()
        );
    }

    protected AbstractColumn(long baseNodeId,
                             String title,
                             String description,
                             String subType,
                             boolean mandatory,
                             ENodeType nodeType,
                             Collection<Long> selectedViewIds) {
        this.baseNodeId = baseNodeId;
        this.title = title;
        this.description = description;
        this.subType = subType;
        this.mandatory = mandatory;
        this.nodeType = nodeType;
        this.selectedViewIds = selectedViewIds;
    }

    public long getBaseNodeId() {
        return baseNodeId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSubType() {
        return subType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public ENodeType getNodeType() {
        return nodeType;
    }

    public Collection<Long> getSelectedViewIds() {
        return selectedViewIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractColumn that = (AbstractColumn) o;
        return baseNodeId == that.baseNodeId && mandatory == that.mandatory && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(subType, that.subType) && nodeType == that.nodeType && Objects.equals(selectedViewIds, that.selectedViewIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseNodeId, title, description, subType, mandatory, nodeType, selectedViewIds);
    }
}
