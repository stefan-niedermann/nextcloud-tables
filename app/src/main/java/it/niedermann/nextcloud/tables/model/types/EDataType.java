package it.niedermann.nextcloud.tables.model.types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.NoSuchElementException;

import it.niedermann.nextcloud.tables.BuildConfig;
import it.niedermann.nextcloud.tables.database.entity.Column;

public enum EDataType {

    UNKNOWN(0, "", ""),

    TEXT(1_000, "text", ""),
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated(since = "0.5.0")
    TEXT_LONG(1_001, "text", "long"),
    TEXT_LINE(1_002, "text", "line"),
    TEXT_LINK(1_003, "text", "link"),
    /** @since 0.5.0 */
    TEXT_RICH(1_004, "text", "rich"),

    DATETIME(2_000, "datetime", ""),
    DATETIME_DATETIME(2_001, "datetime", "datetime"),
    DATETIME_DATE(2_002, "datetime", "date"),
    DATETIME_TIME(2_003, "datetime", "time"),

    SELECTION(3_000, "selection", ""),
    /** @since 0.5.0 */
    SELECTION_MULTI(3_001, "selection", "multi"),
    SELECTION_CHECK(3_002, "selection", "check"),

    NUMBER(4_000, "number", ""),
    NUMBER_PROGRESS(4_001, "number", "progress"),
    NUMBER_STARS(4_002, "number", "stars"),
    ;

    private final int id;
    private final String type;
    private final String subType;

    public static EDataType findById(int id) throws NoSuchElementException {
        for (final var entry : EDataType.values()) {
            if (entry.id == id) {
                return entry;
            }
        }

        throw new NoSuchElementException("Unknown " + EDataType.class.getSimpleName() + " ID: " + id);
    }

    public static EDataType findByColumn(@Nullable Column column) {
        if (column == null) {
            return EDataType.UNKNOWN;
        }

        final var type = column.getType();
        final var subType = column.getSubtype();

        for (final var entry : EDataType.values()) {
            if (entry.type.equals(type) && entry.subType.equals(subType)) {
                return entry;
            }
        }

        if (BuildConfig.DEBUG) {
            throw new UnsupportedOperationException("Unknown column type: " + column.getType() + "/" + column.getSubtype());
        }

        return EDataType.UNKNOWN;
    }

    EDataType(int id, @NonNull String type, @NonNull String subType) {
        this.type = type;
        this.subType = subType;
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
