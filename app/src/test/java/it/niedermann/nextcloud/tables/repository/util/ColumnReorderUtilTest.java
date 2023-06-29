package it.niedermann.nextcloud.tables.repository.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class ColumnReorderUtilTest {

    private ColumnReorderUtil columnReorderUtil;

    @Before
    public void setup() {
        this.columnReorderUtil = new ColumnReorderUtil();
    }

    @Test
    public void reorderColumns_shouldHandleEmptyInputs() {
        final var result = columnReorderUtil.reorderColumns(Collections.emptyMap(), Collections.emptyList());
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void reorderColumns_shouldOnlyChangeMinimal() {
        Map<Long, Integer> result;

        result = columnReorderUtil.reorderColumns(
                Map.of(1L, 50, 2L, 40, 3L, 30, 4L, 20, 5L, 10),
                List.of(1L, 3L, 2L, 4L, 5L)
        );
        Assert.assertEquals(50, (int) result.get(1L));
        Assert.assertEquals(40, (int) result.get(2L));
        Assert.assertEquals(41, (int) result.get(3L));
        Assert.assertEquals(20, (int) result.get(4L));
        Assert.assertEquals(10, (int) result.get(5L));

        result = columnReorderUtil.reorderColumns(
                Map.of(1L, 50, 2L, 40, 3L, 30, 4L, 20, 5L, 10),
                List.of(5L, 4L, 3L, 2L, 1L)
        );
        Assert.assertEquals(54, (int) result.get(5L));
        Assert.assertEquals(53, (int) result.get(4L));
        Assert.assertEquals(52, (int) result.get(3L));
        Assert.assertEquals(51, (int) result.get(2L));
        Assert.assertEquals(50, (int) result.get(1L));

        result = columnReorderUtil.reorderColumns(
                Map.of(1L, 50, 2L, 40, 3L, 30, 4L, 20, 5L, 10),
                List.of(1L, 3L, 2L, 5L, 4L)
        );
        Assert.assertEquals(50, (int) result.get(1L));
        Assert.assertEquals(41, (int) result.get(3L));
        Assert.assertEquals(40, (int) result.get(2L));
        Assert.assertEquals(21, (int) result.get(5L));
        Assert.assertEquals(20, (int) result.get(4L));

        result = columnReorderUtil.reorderColumns(
                Map.of(1L, 50, 2L, 40, 3L, 30, 4L, 20, 5L, 10),
                List.of(4L, 3L, 2L, 1L, 5L)
        );
        Assert.assertEquals(53, (int) result.get(4L));
        Assert.assertEquals(52, (int) result.get(3L));
        Assert.assertEquals(51, (int) result.get(2L));
        Assert.assertEquals(50, (int) result.get(1L));
        Assert.assertEquals(10, (int) result.get(5L));
    }

    @Test
    public void reorderColumns_shouldThrowExceptionForNonUniqueNewColumnOrder() {
        Assert.assertThrows(IllegalArgumentException.class, () -> columnReorderUtil.reorderColumns(
                Map.of(1L, 0, 2L, 0),
                List.of(1L, 1L)
        ));
    }

    @Test
    public void reorderColumns_shouldThrowExceptionForMismatchingKeys() {
        Assert.assertThrows(IllegalArgumentException.class, () -> columnReorderUtil.reorderColumns(
                Map.of(15L, 0),
                List.of(20L)
        ));
        Assert.assertThrows(IllegalArgumentException.class, () -> columnReorderUtil.reorderColumns(
                Collections.emptyMap(),
                List.of(20L)
        ));
        Assert.assertThrows(IllegalArgumentException.class, () -> columnReorderUtil.reorderColumns(
                Map.of(15L, 0),
                Collections.emptyList()
        ));
        Assert.assertThrows(IllegalArgumentException.class, () -> columnReorderUtil.reorderColumns(
                Map.of(15L, 0, 20L, 0),
                List.of(20L)
        ));
        Assert.assertThrows(IllegalArgumentException.class, () -> columnReorderUtil.reorderColumns(
                Map.of(15L, 0),
                List.of(15L, 20L)
        ));
        Assert.assertThrows(IllegalArgumentException.class, () -> columnReorderUtil.reorderColumns(
                Map.of(15L, 0, 20L, 0),
                List.of(15L)
        ));
        Assert.assertThrows(IllegalArgumentException.class, () -> columnReorderUtil.reorderColumns(
                Map.of(20L, 0),
                List.of(15L, 20L)
        ));
    }
}
