package it.niedermann.nextcloud.tables.features.column.manage;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

public class ManageColumnsTouchHelper extends ItemTouchHelper {

    public ManageColumnsTouchHelper(@NonNull ManageColumnsAdapter adapter,
                                    @NonNull Consumer<List<Long>> onDrop
    ) {
        super(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                final int fromPosition = viewHolder.getBindingAdapterPosition();
                final int toPosition = target.getBindingAdapterPosition();

                return adapter.swapVolatile(fromPosition, toPosition);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                onDrop.accept(adapter.getColumnIdsOrderedByViewPosition());
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // We do not support swipe gestures here
            }

        });
    }
}
