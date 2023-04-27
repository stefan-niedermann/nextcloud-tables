package it.niedermann.nextcloud.tables.ui.row;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;

public interface ColumnEditView {

    View getView();

    default Data toData() {
        final var column = getColumn();
        final var data = new Data();
        data.setColumnId(column.getId());
        data.setRemoteColumnId(column.getRemoteId());
        data.setAccountId(column.getAccountId());
        data.setValue(getValue());
        return data;
    }

    @NonNull
    Column getColumn();

    @Nullable
    Object getValue();

    default boolean isValid() {
        return true;
    }
}
