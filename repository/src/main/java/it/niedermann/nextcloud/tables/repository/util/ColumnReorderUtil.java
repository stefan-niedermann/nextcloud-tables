package it.niedermann.nextcloud.tables.repository.util;

import static java.util.Objects.requireNonNullElse;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.tables.database.entity.Column;

public class ColumnReorderUtil {

    /** Fallback value in case a weight is <code>null</code> */
    private static final int NULL_VALUE = 0;

    /**
     * @return key: {@link Column#getId()}, value: target {@link Column#getOrderWeight()}.
     */
    public Map<Long, Integer> reorderColumns(@NonNull Map<Long, Integer> originalOrderWeights,
                                             @NonNull List<Long> newColumnOrder) {

        final var newColumnOrderSet = new HashSet<>(newColumnOrder);

        if (newColumnOrderSet.size() < newColumnOrder.size()) {
            throw new IllegalArgumentException("Elements of newColumnOrder must be unique, but were: " + newColumnOrder);
        }

        if (!originalOrderWeights.keySet().equals(new HashSet<>(newColumnOrderSet))) {
            throw new IllegalArgumentException("Keys of originalOrderWeights and values of newColumnOrder must be equal. originalOrderWeights: " + originalOrderWeights.keySet() + ", newColumnOrder: " + newColumnOrder);
        }

        final var newOrderWeights = new HashMap<>(originalOrderWeights);
        final int size = newColumnOrder.size();
        final int lastIdx = size - 1;

        for (int i = 0; i < size; i = i + 2) {
            final int idx = lastIdx - i;
            final int gapIdx = idx - 1;

            if (idx < 0 || gapIdx < 0) {
                break;
            }

            final int compIdx = idx - 2;
            final Long id = newColumnOrder.get(idx);
            final Long gapId = newColumnOrder.get(gapIdx);
            final int bottom = requireNonNullElse(newOrderWeights.get(id), NULL_VALUE);

            int gap = requireNonNullElse(newOrderWeights.get(gapId), NULL_VALUE);
            if (bottom >= gap) {
                gap = bottom + 1;
                newOrderWeights.put(gapId, gap);
            }

            if (compIdx < 0) {
                break;
            }

            final Long compId = newColumnOrder.get(compIdx);

            if (gap >= requireNonNullElse(newOrderWeights.get(compId), NULL_VALUE)) {
                newOrderWeights.put(compId, gap + 1);
            }
        }

        return newOrderWeights;
    }


    /**
     * @return a {@link Map} containing only elements of {@param newOrderWeight} which differ from {@param originalOrderWeight}.
     */
    public Map<Long, Integer> filterChanged(@NonNull Map<Long, Integer> originalOrderWeights,
                                            @NonNull Map<Long, Integer> newOrderWeights) {

        if (!originalOrderWeights.keySet().equals(newOrderWeights.keySet())) {
            throw new IllegalArgumentException("Keys of originalOrderWeights and newOrderWeights must be equal. originalOrderWeights: " + originalOrderWeights.keySet() + ", newOrderWeights: " + newOrderWeights.keySet());
        }

        final var result = new HashMap<Long, Integer>();

        for (final var newOrderWeight : newOrderWeights.entrySet()) {
            final var originalOrderWeight = originalOrderWeights.get(newOrderWeight.getKey());

            if ((originalOrderWeight != null && !originalOrderWeight.equals(newOrderWeight.getValue()))) {
                result.put(newOrderWeight.getKey(), newOrderWeight.getValue());
            }
        }

        return result;
    }
}
