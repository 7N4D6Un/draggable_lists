package com.mrmelon54.DraggableLists;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class DragManager<T, E extends ObjectSelectionList.Entry<?>> {
    private final DragList<T, E> dragList;
    private DragItem<T, E> selectedItem;
    private double draggingStartX = 0;
    private double draggingStartY = 0;
    private double draggingOffsetX = 0;
    private double draggingOffsetY = 0;
    private double softScrollingTimer = 0;
    private double softScrollingOrigin = 0;

    public DragManager(DragList<T, E> dragList) {
        this.dragList = dragList;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragList.draggable_lists$setDragging(false);
        if (selectedItem == null) return false;

        int dragIdx = dragList.draggable_lists$getIndexOfEntry(selectedItem);
        DragItem<T, E> draggedItem = selectedItem;
        selectedItem = null;
        softScrollingTimer = 0;
        Cursor.reset();
        draggedItem.draggable_lists$setBeingDragged(false);

        double y = capYCoordinate((int) mouseY, true);
        int dropIdx = getIndexFromMouseY(y);

        if (dragIdx >= 0 && dragIdx != dropIdx) {
            dragList.draggable_lists$moveEntry(draggedItem, dropIdx);
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (selectedItem != null || !isCapMouseY((int) mouseY)) return selectedItem != null;

        selectedItem = dragList.draggable_lists$getEntryAtPosition(mouseX, mouseY);
        if (selectedItem == null) return false;

        draggingStartX = mouseX;
        draggingStartY = mouseY;
        draggingOffsetX = dragList.draggable_lists$getRowLeft() - draggingStartX;
        int entryIndex = dragList.draggable_lists$getIndexOfEntry(selectedItem);
        if (entryIndex < 0) {
            selectedItem = null;
            return false;
        }
        draggingOffsetY = dragList.draggable_lists$getRowTop(entryIndex) - draggingStartY;

        if (draggingOffsetX > -32f) {
            selectedItem = null;
            return false;
        }

        dragList.draggable_lists$setDragging(true);
        selectedItem.draggable_lists$getUnderlyingEntry().setFocused(true);
        selectedItem.draggable_lists$setBeingDragged(true);
        softScrollingTimer = 0;
        Cursor.setDragging();
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return selectedItem != null;
    }

    public void renderListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        int rowLeft = (int) dragList.draggable_lists$getRowLeft();
        int rowWidth = dragList.draggable_lists$getRowWidth();
        int rowHeight = dragList.draggable_lists$getItemHeight() - 4;
        int n = dragList.draggable_lists$getItemCount();

        int draggingIndex = selectedItem == null ? -1 : dragList.draggable_lists$getIndexOfEntry(selectedItem);
        int draggedPosition = selectedItem == null ? -1 : getIndexFromMouseY(mouseY);

        for (int i = 0; i < n; i++) {
            int renderRow = otherItemIndexShift(i, draggingIndex, draggedPosition);
            if (renderRow < 0 || renderRow >= n) continue;
            int rowTop = dragList.draggable_lists$getRowTop(renderRow);
            int rowBottom = dragList.draggable_lists$getRowBottom(renderRow);
            if (rowBottom >= dragList.draggable_lists$getY() && rowTop <= dragList.draggable_lists$getBottom()) {
                if (i != draggingIndex) {
                    dragList.draggable_lists$renderItem(guiGraphics, mouseX, mouseY, tickDelta, i, rowLeft, rowTop, rowWidth, rowHeight);
                }
            }
        }
    }

    private int otherItemIndexShift(int i, int draggingIndex, int draggedPosition) {
        if (i == draggingIndex) return draggedPosition;
        if (draggingIndex < draggedPosition) return i >= draggingIndex && i <= draggedPosition ? i - 1 : i;
        return i >= draggedPosition && i <= draggingIndex ? i + 1 : i;
    }


    public void renderWidget(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        if (selectedItem == null) return;

        int z = Mth.floor(mouseY + draggingOffsetY);
        int x = Mth.floor(draggingStartX + draggingOffsetX);
        int y = capYCoordinate(z);
        int entryHeight = dragList.draggable_lists$getItemHeight() - 4;
        int entryWidth = dragList.draggable_lists$getRowWidth();

        guiGraphics.pose().pushMatrix();
        float shaderColorValue = 191f / 255f;
        guiGraphics.fill(x - 1, y - 1, x + entryWidth - 2, y + entryHeight + 1, ARGB.colorFromFloat(0.5f, shaderColorValue, shaderColorValue, shaderColorValue));
        selectedItem.draggable_lists$render(guiGraphics, x, y, entryWidth, entryHeight, mouseX, mouseY, false, delta);
        guiGraphics.pose().popMatrix();

        if (y < z) {
            if (softScrollingTimer == 0) {
                softScrollingTimer = Util.getMillis();
                softScrollingOrigin = dragList.draggable_lists$getScrollAmount();
            }
            float f = (float) (Util.getMillis() - softScrollingTimer) / 5f;
            dragList.draggable_lists$setScrollAmount(softScrollingOrigin + f);
        } else if (y > z) {
            if (softScrollingTimer == 0) {
                softScrollingTimer = Util.getMillis();
                softScrollingOrigin = dragList.draggable_lists$getScrollAmount();
            }
            float f = (float) (Util.getMillis() - softScrollingTimer) / 5f;
            dragList.draggable_lists$setScrollAmount(softScrollingOrigin - f);
        } else {
            softScrollingTimer = 0;
        }
    }

    private int capYCoordinate(int y, boolean useScreenSpace) {
        int scrollableTop = dragList.draggable_lists$getY() + 4;
        int scrollableHeight = dragList.draggable_lists$getBottom() - dragList.draggable_lists$getY() - (useScreenSpace ? 2 : dragList.draggable_lists$getItemHeight() + 2);
        if (y < scrollableTop) y = scrollableTop;
        if (y > scrollableTop + scrollableHeight) y = scrollableTop + scrollableHeight;
        return y;
    }

    private int capYCoordinate(int y) {
        return capYCoordinate(y, false);
    }

    private boolean isCapMouseY(int y) {
        return capYCoordinate(y, true) == y;
    }

    private int getIndexFromMouseY(double mouseY) {
        int n = dragList.draggable_lists$getItemCount();
        for (int i = 0; i < n; i++) {
            if (mouseY < dragList.draggable_lists$getRowBottom(i)) return i;
        }
        return n;
    }
}
