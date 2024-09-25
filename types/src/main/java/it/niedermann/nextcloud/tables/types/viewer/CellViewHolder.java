package it.niedermann.nextcloud.tables.types.viewer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import java.util.Optional;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.database.entity.Data;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;

public abstract class CellViewHolder extends AbstractViewHolder {

    @NonNull
    private final DefaultValueSupplier defaultValueSupplier;

    public CellViewHolder(@NonNull View itemView,
                          @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(itemView);
        this.defaultValueSupplier = defaultValueSupplier;
    }

    public final void bind(@Nullable Data data, @NonNull Column column) {
        final var value = data == null ? defaultValueSupplier.getDefaultValue(column) : data.getValue();
        this.bind(value == null ? JsonNull.INSTANCE : value, column);
    }

    protected abstract void bind(@NonNull JsonElement value, @NonNull Column column);

    @NonNull
    public Optional<QuickActionProvider> getQuickActionProvider() {
        return Optional.empty();
    }

    public record QuickActionProvider(@StringRes int title) {
    }
}
