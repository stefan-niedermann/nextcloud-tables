package it.niedermann.nextcloud.tables.database.model;

import static java.util.Collections.emptySet;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import it.niedermann.nextcloud.tables.shared.FeatureToggle;

public enum EDataType {

    UNKNOWN(0, ""),

    /** @noinspection DeprecatedIsStillUsed*/
    @Deprecated(since = "0.5.0")
    TEXT_LONG(1_000, "text", "long"),
    TEXT_LINE(1_001, "text", "line"),
    TEXT_LINK(1_002, "text", "link"),
    /** @since 0.5.0 */
    TEXT_RICH(1_003, "text", "rich"),

    DATETIME(2_000, "datetime"),
    DATETIME_DATE(2_001, "datetime", "date"),
    DATETIME_TIME(2_002, "datetime", "time"),

    SELECTION(3_000, "selection"),
    /** @since 0.5.0 */
    SELECTION_MULTI(3_001, "selection", "multi"),
    SELECTION_CHECK(3_002, "selection", "check"),

    NUMBER(4_000, "number"),
    NUMBER_PROGRESS(4_001, "number", "progress"),
    NUMBER_STARS(4_002, "number", "stars"),

    /** @since 0.8.0 */
    USERGROUP(5_000, "usergroup"),
    ;

    private final int id;
    private final String type;
    private final String subType;

    private static final Collection<EDataType> DATA_TYPES_USING_SELECTION_OPTIONS = Set.of(SELECTION, SELECTION_MULTI);
    private static final Collection<EDataType> DATA_TYPES_USING_USER_GROUPS = Set.of(USERGROUP);

    public static EDataType findById(int id) throws NoSuchElementException {
        for (final var entry : EDataType.values()) {
            if (entry.id == id) {
                return entry;
            }
        }

        throw new NoSuchElementException("Unknown " + EDataType.class.getSimpleName() + " ID: " + id);
    }

    public static EDataType findByType(@Nullable String type, @Nullable String subType) {
        for (final var entry : EDataType.values()) {
            if (entry.type.equals(type) && Objects.equals(entry.subType, subType)) {
                return entry;
            }
        }

        if (FeatureToggle.STRICT_MODE.enabled) {
            throw new UnsupportedOperationException("Unknown column type: " + type + "/" + subType);
        }

        return EDataType.UNKNOWN;
    }

    EDataType(int id,
              @NonNull String type) {
        this(id, type, null);
    }

    EDataType(int id,
              @NonNull String type,
              @Nullable String subType) {
        this.id = id;
        this.type = type;
        this.subType = subType;
    }

    public int getId() {
        return this.id;
    }

    @NonNull
    public String getType() {
        return this.type;
    }

    @NonNull
    public Optional<String> getSubType() {
        return Optional.ofNullable(this.subType);
    }

    @NonNull
    public static Collection<String> getTypes() {
        return Arrays
                .stream(values())
                .map(value -> value.type)
                .filter(not(TextUtils::isEmpty))
                .collect(toUnmodifiableSet());
    }

    /// @return a [Collection] of other [EDataType]s with the same [#@param type].
    /// Blank sub types are only returned if there are non-blank sub types, too.
    ///
    /// ```
    /// type1/
    /// → []
    ///
    /// type2/
    /// type2/subtype1
    /// → [null, "subtype2"]
    ///
    /// type3/subtype1
    /// → ["subtype1"]
    ///
    /// type4/subtype1
    /// type4/subtype2
    /// → ["subtype1", "subtype"]
    ///```
    @NonNull
    public static Collection<EDataType> getTypeVariants(@NonNull String type) {
        final var sameTypeCollection = Arrays.stream(values())
                .filter(value -> value.type.equals(type))
                .collect(toUnmodifiableSet());

        if (sameTypeCollection.size() == 1) {
            return emptySet();
        }

        return sameTypeCollection;
    }

    public boolean hasSelectionOptions() {
        return DATA_TYPES_USING_SELECTION_OPTIONS.contains(this);
    }

    public boolean hasUserGroups() {
        return DATA_TYPES_USING_USER_GROUPS.contains(this);
    }

    @NonNull
    @Override
    public String toString() {
        if (this == UNKNOWN) {
            return "unknown";
        }

        return type + getSubType().map(subType -> "/" + subType).orElse("");
    }
}
