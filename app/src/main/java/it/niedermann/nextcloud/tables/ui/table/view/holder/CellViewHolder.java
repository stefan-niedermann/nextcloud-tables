package it.niedermann.nextcloud.tables.ui.table.view.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.model.types.EDataType;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime.DateCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime.DateTimeCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.datetime.TimeCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.number.NumberCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.number.ProgressCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.number.StarsCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.selection.CheckCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.selection.SelectionViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.text.LineCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.text.LongCellViewHolder;
import it.niedermann.nextcloud.tables.ui.table.view.holder.type.text.TextCellViewHolder;

public abstract class CellViewHolder extends AbstractViewHolder {

    public CellViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nullable Data data, @NonNull Column column);

    @NonNull
    public Optional<QuickActionProvider> getQuickActionProvider() {
        return Optional.empty();
    }

    public static class QuickActionProvider {

        @StringRes
        private final int title;

        public QuickActionProvider(@StringRes int title) {
            this.title = title;
        }

        @StringRes
        public int getTitle() {
            return title;
        }
    }

    public static class Factory {

        @NonNull
        public CellViewHolder create(@NonNull EDataType dataType, @NonNull ViewGroup parent) {
            final var layoutInflater = LayoutInflater.from(parent.getContext());
            switch (dataType) {
                case TEXT_RICH:
                case TEXT_LONG:
                    return new LongCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
                case TEXT_LINE:
                    return new LineCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
                case DATETIME:
                case DATETIME_DATETIME:
                    return new DateTimeCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
                case DATETIME_DATE:
                    return new DateCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
                case DATETIME_TIME:
                    return new TimeCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
                case SELECTION:
                    return new SelectionViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
                case SELECTION_CHECK:
                    return new CheckCellViewHolder(TableviewCellCheckBinding.inflate(layoutInflater, parent, false));
                case NUMBER:
                    return new NumberCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
                case NUMBER_PROGRESS:
                    return new ProgressCellViewHolder(TableviewCellProgressBinding.inflate(layoutInflater, parent, false));
                case NUMBER_STARS:
                    return new StarsCellViewHolder(TableviewCellStarsBinding.inflate(layoutInflater, parent, false));
                case TEXT:
                case TEXT_LINK:
                case UNKNOWN:
                default:
                    return new TextCellViewHolder(TableviewCellBinding.inflate(layoutInflater, parent, false));
            }
        }
    }
}
