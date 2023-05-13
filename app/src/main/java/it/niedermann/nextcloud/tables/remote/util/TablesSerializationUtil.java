package it.niedermann.nextcloud.tables.remote.util;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TablesSerializationUtil {

    /**
     * Adds <code>[</code> and <code>]</code> brackets around a comma separated list of values.
     */
    @Nullable
    public String serializeArray(@Nullable String value) {
        if (value == null) {
            return null;
        }

        return value.isBlank()
                ? "[]"
                : "[" + value + "]";
    }

    /**
     * Removes <code>[</code> and <code>]</code> brackets from a comma separated list of {@link Long} values.
     */
    @Nullable
    public String deserializeArray(@Nullable String value) {
        if (value == null) {
            return null;
        }

        final var valueWithoutJsonArrayBrackets = value
                .replace("[", "")
                .replace("]", "");

        if (valueWithoutJsonArrayBrackets.isBlank()) {
            return "";
        }

        return Arrays.stream(valueWithoutJsonArrayBrackets.split(","))
                .map(Double::parseDouble)
                .map(Double::longValue)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
