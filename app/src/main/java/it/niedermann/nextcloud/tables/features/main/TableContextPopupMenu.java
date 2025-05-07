package it.niedermann.nextcloud.tables.features.main;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.Table;

///
public class TableContextPopupMenu extends PopupMenu {

    private final Table table;

    public TableContextPopupMenu(
            @NonNull Context context,
            @NonNull View anchor,
            @NonNull Table table
    ) {
        super(context, anchor);
        this.table = table;
        this.inflate(R.menu.context_menu_table);
        this.prepare();
    }

    private void prepare() {
        final var menu = getMenu();

        if (table.isFavorite()) {
            menu.findItem(R.id.favorite_table).setTitle(R.string.remove_table_from_favorites);
            menu.findItem(R.id.archive_table).setVisible(false);

        } else if (table.isArchived()) {
            menu.findItem(R.id.favorite_table).setVisible(false);
            menu.findItem(R.id.archive_table).setTitle(R.string.unarchive_table);

        } else {
            menu.findItem(R.id.favorite_table).setTitle(R.string.add_table_to_favorites);
            menu.findItem(R.id.archive_table).setTitle(R.string.archive_table);

        }
    }
}
