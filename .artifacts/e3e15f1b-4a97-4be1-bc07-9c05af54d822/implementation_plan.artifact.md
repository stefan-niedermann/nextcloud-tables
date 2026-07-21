# Scroll State Restoration Improvement in ViewTableFragment

The current scroll state restoration in `ViewTableFragment` snaps the first visible item to the top of the viewport, losing the pixel offset and potentially cutting off rows at the bottom. Additionally, the scroll position is reset to zero every time the UI state is applied, which can cause jumps during data updates.

## User Review Required

> [!IMPORTANT]
> The fix involves changing the scroll restoration logic to include pixel offsets and conditional resets. This should make scrolling smoother and more persistent.

## Proposed Changes

### [app]

#### [MODIFY] [ViewTableFragment.java](file:///home/stefan/AndroidStudioProjects/nextcloud-tables/app/src/main/java/it/niedermann/nextcloud/tables/features/table/view/ViewTableFragment.java)

- Add a `lastTableId` field to track table changes.
- Modify `applyUiState` to:
    - Capture current `rowPosition`, `columnPosition` and their respective pixel offsets BEFORE updating the adapter.
    - Only reset scroll to (0,0) if `fullTable.getTable().getId()` differs from `lastTableId`.
    - Restore scroll using `scrollToRowPosition(position, offset)` and `scrollToColumnPosition(position, offset)` on the `TableView`'s `ScrollHandler` AFTER updating the adapter.
    - Remove the unconditional `scrollToRowPosition(0)` and `scrollToColumnPosition(0)` calls at the beginning of the method.

## Verification Plan

### Automated Tests
- Build the project to ensure no regressions in existing logic.
- Run `gradle_build(":app:assembleDebug")`.

### Manual Verification
1. Open a table and scroll to a middle position where a row is partially visible at the top.
2. Trigger a data update (e.g. by editing a cell or refreshing).
3. Verify that the scroll position is maintained exactly, including the pixel offset.
4. Switch to a different table and verify that it starts at the top (scroll reset).
5. Verify that no rows are "cut off" at the bottom after restoration.
