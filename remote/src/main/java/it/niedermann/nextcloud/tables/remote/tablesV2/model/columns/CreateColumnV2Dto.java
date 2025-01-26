package it.niedermann.nextcloud.tables.remote.tablesV2.model.columns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import it.niedermann.nextcloud.tables.remote.tablesV2.model.ColumnV2Dto;
import it.niedermann.nextcloud.tables.remote.tablesV2.model.ENodeTypeV2Dto;

public class CreateColumnV2Dto {

    private final long baseNodeId;
    @NonNull
    private final String title;
    @Nullable
    private final String description;
    @Nullable
    private final String subtype;
    private final int mandatory;
    @NonNull
    private final ENodeTypeV2Dto baseNodeType;
    @Nullable
    private final Collection<Long> selectedViewIds;

    protected CreateColumnV2Dto(long tableRemoteId, @NonNull ColumnV2Dto column) {
        this(
                tableRemoteId,
                column.title(),
                column.description(),
                column.subtype(),
                column.mandatory(),
                ENodeTypeV2Dto.TABLE,
                Collections.emptySet()
        );
    }

    protected CreateColumnV2Dto(long baseNodeId,
                                @NonNull String title,
                                @Nullable String description,
                                @Nullable String subtype,
                                @Nullable Boolean mandatory,
                                @NonNull ENodeTypeV2Dto baseNodeType,
                                @Nullable Collection<Long> selectedViewIds) {
        this.baseNodeId = baseNodeId;
        this.title = title;
        this.description = description;
        this.subtype = subtype;
        this.mandatory = Boolean.TRUE.equals(mandatory) ? 1 : 0;
        this.baseNodeType = baseNodeType;
        this.selectedViewIds = selectedViewIds;
    }

    public long getBaseNodeId() {
        return baseNodeId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getSubtype() {
        return subtype;
    }

    public int getMandatory() {
        return mandatory;
    }

    @NonNull
    public ENodeTypeV2Dto getBaseNodeType() {
        return baseNodeType;
    }

    @Nullable
    public Collection<Long> getSelectedViewIds() {
        return selectedViewIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateColumnV2Dto that = (CreateColumnV2Dto) o;
        return baseNodeId == that.baseNodeId && mandatory == that.mandatory && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(subtype, that.subtype) && baseNodeType == that.baseNodeType && Objects.equals(selectedViewIds, that.selectedViewIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseNodeId, title, description, subtype, mandatory, baseNodeType, selectedViewIds);
    }
}
