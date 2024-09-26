package it.niedermann.nextcloud.tables.remote.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.SelectionOption;
import it.niedermann.nextcloud.tables.database.model.SelectionDefault;

public class ColumnAdapter {

    public int serializeMandatory(boolean mandatory) {
        return Boolean.TRUE.equals(mandatory) ? 1 : 0;
    }

    @NonNull
    public String serializeSelectionDefault(@Nullable SelectionDefault selectionDefault) {
        return serializeSelectionDefault(
                selectionDefault == null ? null : selectionDefault.getValue());
    }

    @NonNull
    private String serializeSelectionDefault(@Nullable JsonElement value) {
        if (value == null) {
            return "";
        }

        return new JsonPrimitive(value.toString()).toString();
    }

    @NonNull
    public SelectionDefault deserializeSelectionDefault(@Nullable SelectionDefault selectionDefault) {
        return new SelectionDefault(deserializeSelectionDefault(
                selectionDefault == null ? null : selectionDefault.getValue()));
    }

    @NonNull
    private JsonElement deserializeSelectionDefault(@Nullable JsonElement value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        }

        if (value == JsonNull.INSTANCE) {
            return JsonNull.INSTANCE;
        }

        final var str = value.getAsString();
        return JsonParser.parseString(str);
    }

    @NonNull
    public String serializeSelectionOptions(@Nullable Collection<SelectionOption> selectionOptions) {
        final var options = new JsonArray();

        if (selectionOptions != null) {
            for (final var option : selectionOptions) {
                options.add(option.getRemoteId());
            }
        }

        return options.toString();
    }

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

//
//        options.add
////        final var type = EDataType.findByColumn(column);
////
////        if (type == EDataType.SELECTION_MULTI) {
//        return "[" + column.getSelectionOptions()
//                .stream()
//                .map(AbstractRemoteEntity::getRemoteId)
//                .map(String::valueOf)
//                .collect(Collectors.joining(",")) + "]";
////        }

//        return "[]";
//    }

//    @Nullable
//    public String serializeSelectionDefault(@NonNull Column column) {
//        final var type = EDataType.findByColumn(column);
//
//        if (type == EDataType.SELECTION_MULTI) {
//            return util.serializeArray(column.getSelectionDefault());
//        }
//
//        return column.getSelectionDefault();
//    }

//    @Nullable
//    public String deserializeSelectionDefault(@NonNull Column column) {
//        final var type = EDataType.findByColumn(column);
//
//        if (type == EDataType.SELECTION_MULTI) {
//            final var selectionDefault = column.getSelectionDefault();
//
//            if (selectionDefault == null) {
//                return null;
//            }
//
//            if ("null".equals(selectionDefault)) {
//                return null; // API returns this as String...
//            }
//
//            return util.deserializeArray(selectionDefault.replaceAll("\"", ""));
//        }
//
//        return column.getSelectionDefault();
//    }
}
