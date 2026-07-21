# Walkthrough: Scroll State Restoration Improvement

I have improved the scroll state restoration in `ViewTableFragment` to ensure that the exact scroll position, including pixel-level offsets, is preserved during data updates and that the scroll is only reset when switching tables.

## Changes

### [app]

#### [ViewTableFragment.java](file:///home/stefan/AndroidStudioProjects/nextcloud-tables/app/src/main/java/it/niedermann/nextcloud/tables/features/table/view/ViewTableFragment.java)

- Added a `lastTableId` field to track which table is currently being viewed.
- Updated `applyUiState` to:
    - Capture the current row and column positions and their respective pixel offsets before updating the table adapter.
    - Conditionally reset the scroll position: it only resets to (0,0) if the table ID has changed.
    - Restore the scroll position using `scrollToRowPosition(position, offset)` and `scrollToColumnPosition(position, offset)` after the adapter has been updated with new data.
    - Removed the unconditional scroll-to-top logic that was previously causing jumps.

```java
        if (lastTableId != null && lastTableId == fullTable.getTable().getId()) {
            rowPosition = binding.tableView.getCellLayoutManager().findFirstVisibleItemPosition();
            final var firstRowView = binding.tableView.getCellLayoutManager().getChildAt(0);
            rowOffset = (firstRowView != null) ? firstRowView.getTop() : 0;

            columnPosition = binding.tableView.getColumnHeaderLayoutManager().findFirstVisibleItemPosition();
            final var firstColumnView = binding.tableView.getColumnHeaderLayoutManager().getChildAt(0);
            columnOffset = (firstColumnView != null) ? firstColumnView.getLeft() : 0;
        } else {
            rowPosition = 0;
            rowOffset = 0;
            columnPosition = 0;
            columnOffset = 0;
            lastTableId = fullTable.getTable().getId();
        }
```

## Verification Results

### Automated Tests
- Ran `:app:assembleDebug` and the build finished successfully.

### Manual Verification Required
- Verify that scrolling is now persistent when data updates (e.g., after editing a cell).
- Verify that switching tables still resets the scroll position to the top.
