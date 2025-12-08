package it.niedermann.nextcloud.tables.util;

import androidx.annotation.NonNull;

import java.util.Locale;

import it.niedermann.nextcloud.tables.database.entity.Table;

public class TableFormatter {

    public static String getTitleWithEmoji(@NonNull Table table) {
        return String.format(Locale.getDefault(), "%s %s", table.getEmoji(), table.getTitle()).trim();
    }
}
