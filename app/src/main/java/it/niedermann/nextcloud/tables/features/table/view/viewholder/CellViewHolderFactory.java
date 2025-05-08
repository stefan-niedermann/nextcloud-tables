package it.niedermann.nextcloud.tables.features.table.view.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.database.model.DataTypeServiceRegistry;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.databinding.TableviewCellBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellCheckBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellProgressBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellRichBinding;
import it.niedermann.nextcloud.tables.databinding.TableviewCellStarsBinding;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.datetime.DateCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.datetime.DateTimeCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.datetime.TimeCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.number.NumberCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.number.ProgressCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.number.StarsCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.selection.SelectionCheckCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.selection.SelectionMultiViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.selection.SelectionViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.text.LinkCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.text.LongCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.text.RichViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.text.TextCellViewHolder;
import it.niedermann.nextcloud.tables.features.table.view.viewholder.types.usergroup.UserGroupViewHolder;
import it.niedermann.nextcloud.tables.repository.defaults.DefaultValueSupplier;

public class CellViewHolderFactory {

    private final DataTypeServiceRegistry<DefaultValueSupplier> defaultSupplierServiceRegistry;

    public CellViewHolderFactory(
            @NonNull DataTypeServiceRegistry<DefaultValueSupplier> defaultSupplierServiceRegistry
    ) {
        this.defaultSupplierServiceRegistry = defaultSupplierServiceRegistry;
    }

    public CellViewHolder getService(@NonNull EDataType dataType, @NonNull ViewGroup parent) {
        final var defaultValueSupplier = defaultSupplierServiceRegistry.getService(dataType);
        return switch (dataType) {
            case TEXT_LINE, UNKNOWN ->
                    new TextCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case TEXT_LINK ->
                    new LinkCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case TEXT_LONG ->
                    new LongCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case TEXT_RICH ->
                    new RichViewHolder(TableviewCellRichBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case DATETIME ->
                    new DateTimeCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case DATETIME_DATE ->
                    new DateCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case DATETIME_TIME ->
                    new TimeCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case SELECTION ->
                    new SelectionViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case SELECTION_MULTI ->
                    new SelectionMultiViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case SELECTION_CHECK ->
                    new SelectionCheckCellViewHolder(TableviewCellCheckBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case NUMBER ->
                    new NumberCellViewHolder(TableviewCellBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case NUMBER_PROGRESS ->
                    new ProgressCellViewHolder(TableviewCellProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case NUMBER_STARS ->
                    new StarsCellViewHolder(TableviewCellStarsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
            case USERGROUP ->
                    new UserGroupViewHolder(TableviewCellRichBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), defaultValueSupplier);
        };
    }
}
