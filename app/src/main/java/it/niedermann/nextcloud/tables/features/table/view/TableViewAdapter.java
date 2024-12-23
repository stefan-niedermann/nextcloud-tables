package it.niedermann.nextcloud.tables.features.table.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.database.model.FullColumn;
import it.niedermann.nextcloud.tables.database.model.FullData;
import it.niedermann.nextcloud.tables.database.model.FullRow;
import it.niedermann.nextcloud.tables.databinding.TableviewColumnHeaderBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCornerBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewRowHeaderBinding;
import it.niedermann.nextcloud.tables.features.table.view.types.CellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.types.ViewHolderFactory;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.ColumnHeaderViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.RowHeaderViewHolder;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;

public class TableViewAdapter extends AbstractTableAdapter<FullColumn, FullRow, FullData> {

    private final DataTypeServiceRegistry<ViewHolderFactory> registry;

    public TableViewAdapter(@NonNull DataTypeServiceRegistry<ViewHolderFactory> registry) {
        this.registry = registry;
    }

    @Override
    public int getCellItemViewType(int columnPosition) {
        final var fullColumn = getColumnHeaderItem(columnPosition);
        if (fullColumn == null) {
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw new IllegalStateException(FullColumn.class.getSimpleName() + " header item on position " + columnPosition + " is null. Can not determine " + EDataType.class.getSimpleName());
            } else {
                return EDataType.UNKNOWN.getId();
            }
        }

        return fullColumn.getColumn().getDataType().getId();
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        return registry.getService(EDataType.findById(viewType)).create(parent);
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder,
                                     @Nullable FullData cellItemModel,
                                     int columnPosition,
                                     int rowPosition) {
        final var fullColumn = getColumnHeaderItem(columnPosition);

        try {
            if (fullColumn == null) {
                throw new NullPointerException(FullColumn.class.getSimpleName() + " header was null for [columnPosition: " + columnPosition + " / rowPosition: " + rowPosition + "]");
            }

            final var column = fullColumn.getColumn();

            if (column == null) {
                throw new NullPointerException(Column.class.getSimpleName() + " header was null for [columnPosition: " + columnPosition + " / rowPosition: " + rowPosition + "]");
            }

            if (cellItemModel == null) {
                throw new NullPointerException("cellItemModel was null for [columnPosition: " + columnPosition + " / rowPosition: " + rowPosition + "]");
            }

            if (holder instanceof CellViewHolder cellViewHolder) {
                cellViewHolder.bind(cellItemModel, column);

            } else {
                throw new IllegalArgumentException("Unknown view holder type " + holder);
            }

        } catch (Exception e) {
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw e;
            }
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColumnHeaderViewHolder(TableviewColumnHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable FullColumn fullColumn, int columnPosition) {
        try {
            if (fullColumn == null) {
                throw new NullPointerException(FullColumn.class.getSimpleName() + " is null.");
            }

            final var column = fullColumn.getColumn();

            if (column == null) {
                throw new NullPointerException(Column.class.getSimpleName() + " is null.");
            }

            if (holder instanceof ColumnHeaderViewHolder) {
                ((ColumnHeaderViewHolder) holder).bind(column);
            } else {
                throw new IllegalArgumentException("Unknown view holder type " + holder);
            }
        } catch (Exception e) {
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw e;
            }
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RowHeaderViewHolder(TableviewRowHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable FullRow rowHeaderItemModel, int rowPosition) {
        try {
            if (rowHeaderItemModel == null) {
                throw new NullPointerException("columnHeaderItemModel is null.");
            }

            if (holder instanceof RowHeaderViewHolder) {
                ((RowHeaderViewHolder) holder).bind(rowHeaderItemModel.getRow());
            } else {
                throw new IllegalArgumentException("Unknown view holder type " + holder);
            }
        } catch (Exception e) {
            if (FeatureToggle.STRICT_MODE.enabled) {
                throw e;
            }
        }
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return TableviewCornerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false).getRoot();
    }
}
