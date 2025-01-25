package it.niedermann.nextcloud.tables.database.model;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import it.niedermann.nextcloud.tables.database.R;
import it.niedermann.nextcloud.tables.shared.FeatureToggle;

public enum EDataType implements Comparable<EDataType> {

    UNKNOWN(0, EDataTypeGroup.UNKNOWN),

    /// @noinspection DeprecatedIsStillUsed
    @Deprecated(since = "0.5.0")
    TEXT_LONG(1_000, EDataTypeGroup.TEXT, "long", R.string.subtype_text_long),
    TEXT_LINE(1_001, EDataTypeGroup.TEXT, "line", R.string.subtype_text_line),
    TEXT_LINK(1_002, EDataTypeGroup.TEXT, "link", R.string.subtype_text_link),
    /// @since 0.5.0
    TEXT_RICH(1_003, EDataTypeGroup.TEXT, "rich", R.string.subtype_text_rich),

    DATETIME(2_000, EDataTypeGroup.DATETIME),
    DATETIME_DATE(2_001, EDataTypeGroup.DATETIME, "date", R.string.subtype_datetime_date),
    DATETIME_TIME(2_002, EDataTypeGroup.DATETIME, "time", R.string.subtype_datetime_time),

    SELECTION(3_000, EDataTypeGroup.SELECTION),
    /// @since 0.5.0
    SELECTION_MULTI(3_001, EDataTypeGroup.SELECTION, "multi", R.string.subtype_selection_multi),
    SELECTION_CHECK(3_002, EDataTypeGroup.SELECTION, "check", R.string.subtype_selection_check),

    NUMBER(4_000, EDataTypeGroup.NUMBER),
    NUMBER_PROGRESS(4_001, EDataTypeGroup.NUMBER, "progress", R.string.subtype_number_progress),
    NUMBER_STARS(4_002, EDataTypeGroup.NUMBER, "stars", R.string.subtype_number_stars),

    /// @since 0.8.0
    USERGROUP(5_000, EDataTypeGroup.USERGROUP),
    ;

    private static final Collection<EDataType> DATA_TYPES_USING_SELECTION_OPTIONS = Set.of(SELECTION, SELECTION_MULTI);
    private static final Collection<EDataType> DATA_TYPES_USING_USER_GROUPS = Set.of(USERGROUP);
    private static final Collection<EDataType> DATA_TYPES_USING_LINK_VALUE = Set.of(TEXT_LINK);

    private final int id;

    @NonNull
    public final EDataTypeGroup group;

    @Nullable
    private final String subType;

    @StringRes
    @Nullable
    private final Integer humanReadableSubTypeStringRes;

    EDataType(int id, @NonNull EDataTypeGroup group) {
        this(id, group, null, null);
    }

    EDataType(int id,
              @NonNull EDataTypeGroup group,
              @Nullable String subType,
              @StringRes @Nullable Integer humanReadableSubTypeStringRes) {
        this.id = id;
        this.group = group;
        this.subType = subType;
        this.humanReadableSubTypeStringRes = humanReadableSubTypeStringRes;
    }

    public static EDataType findById(int id) throws NoSuchElementException {
        for (final var entry : EDataType.values()) {
            if (entry.id == id) {
                return entry;
            }
        }

        throw new NoSuchElementException("Unknown " + EDataType.class.getSimpleName() + " ID: " + id);
    }

    public static EDataType findByType(@Nullable String type, @Nullable String subType) {
        final var group = EDataTypeGroup.findByType(Optional.ofNullable(type).orElse(""));

        for (final var entry : EDataType.values()) {
            if (entry.group != group) {
                continue;
            }

            if (Objects.equals(
                    Optional.ofNullable(entry.subType).orElse(""),
                    Optional.ofNullable(subType).orElse(""))
            ) {
                return entry;
            }
        }

        if (FeatureToggle.STRICT_MODE.enabled) {
            throw new UnsupportedOperationException("Unknown column type: " + type + "/" + subType);
        }

        return EDataType.UNKNOWN;
    }

    public int getId() {
        return this.id;
    }

    public Optional<Integer> getHumanReadableSubTypeStringRes() {
        return Optional.ofNullable(humanReadableSubTypeStringRes);
    }

    @NonNull
    public Optional<String> getSubType() {
        return Optional.ofNullable(this.subType);
    }

    public boolean hasSelectionOptions() {
        return DATA_TYPES_USING_SELECTION_OPTIONS.contains(this);
    }

    public boolean hasUserGroups() {
        return DATA_TYPES_USING_USER_GROUPS.contains(this);
    }

    public boolean hasLinkValue() {
        return DATA_TYPES_USING_LINK_VALUE.contains(this);
    }

    @NonNull
    public String getType() {
        return group.value;
    }

    @NonNull
    @Override
    public String toString() {
        return getSubType()
                .filter(not(String::isBlank))
                .map(st -> group + "/" + st)
                .orElseGet(group::toString);
    }

    @NonNull
    public String toHumanReadableString(@NonNull Context context) {
        return Optional.ofNullable(humanReadableSubTypeStringRes)
                .map(st -> group.toHumanReadableString(context) + " / " + context.getString(st))
                .orElse(group.toHumanReadableString(context));
    }

    public enum EDataTypeGroup {
        UNKNOWN("", R.string.type_unknown),
        TEXT("text", R.string.type_text),
        SELECTION("selection", R.string.type_selection),
        DATETIME("datetime", R.string.type_datetime),
        NUMBER("number", R.string.type_number),
        USERGROUP("usergroup", R.string.type_usergroup),
        ;

        public final String value;
        @StringRes
        public final int humanReadableValue;

        EDataTypeGroup(
                @NonNull String value,
                @StringRes int humanReadableValue
        ) {
            this.value = value;
            this.humanReadableValue = humanReadableValue;
        }

        @NonNull
        public static EDataTypeGroup findByType(@NonNull String type) {
            for (final var group : values()) {
                if (group.value.equals(type)) {
                    return group;
                }
            }

            if (FeatureToggle.STRICT_MODE.enabled) {
                throw new UnsupportedOperationException("Unknown " + EDataTypeGroup.class.getSimpleName() + ": " + type);
            }

            return EDataTypeGroup.UNKNOWN;
        }

        /// @return all [EDataType]s belonging to this [EDataTypeGroup].
        @NonNull
        public Collection<EDataType> getDataTypes() {
            return Arrays.stream(EDataType.values())
                    .filter(value -> value.group.equals(EDataTypeGroup.this))
                    .collect(toUnmodifiableSet());
        }

        @NonNull
        @Override
        public String toString() {
            return value;
        }

        @NonNull
        public String toHumanReadableString(@NonNull Context context) {
            return context.getString(humanReadableValue);
        }
    }
}
