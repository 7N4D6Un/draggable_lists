package com.mrmelon54.DraggableLists;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class DragManager<T, E extends ObjectSelectionList.Entry<?>> {
    private static final int RENDER_MARGIN = 4;
    private static final int DRAG_AREA_TOP_INSET = 4;
    private static final int DRAG_AREA_BOTTOM_INSET = 2;
    private static final int ICON_COLUMN_WIDTH = 32;
    private static final double DRAG_START_THRESHOLD_SQUARED = 9.0;
    private static final float SOFT_SCROLL_PIXELS_PER_MS = 1f / 5f;

    private final DragList<T, E> dragList;
    private DragItem<T, E> selectedItem;
    private DragItem<T, E> dragCandidate;
    private double draggingStartX = 0;
    private double draggingStartY = 0;
    private double draggingOffsetX = 0;
    private double draggingOffsetY = 0;
    private long softScrollingTimer = 0;
    private double softScrollingOrigin = 0;

    public DragManager(DragList<T, E> dragList) {
        this.dragList = dragList;
    }

    public boolean isDragging() {
        return selectedItem != null;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragList.draggable_lists$setDragging(false);
        dragCandidate = null;
        if (selectedItem == null) return false;

        int dragIdx = dragList.draggable_lists$getIndexOfEntry(selectedItem);
        DragItem<T, E> draggedItem = selectedItem;
        selectedItem = null;
        softScrollingTimer = 0;
        draggedItem.draggable_lists$setBeingDragged(false);

        int dropIdx = getIndexFromMouseY(capMouseY((int) mouseY));
        if (dragIdx >= 0 && dragIdx != dropIdx) {
            dragList.draggable_lists$moveEntry(draggedItem, dropIdx);
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (selectedItem != null) return true;
        if (!isMouseYInDragArea((int) mouseY)) {
            dragCandidate = null;
            return false;
        }
        if (dragCandidate == null) {
            return captureDragCandidate(mouseX, mouseY);
        }

        double dx = mouseX - draggingStartX;
        double dy = mouseY - draggingStartY;
        if (dx * dx + dy * dy < DRAG_START_THRESHOLD_SQUARED) return true;

        selectedItem = dragCandidate;
        dragCandidate = null;
        dragList.draggable_lists$setDragging(true);
        selectedItem.draggable_lists$getUnderlyingEntry().setFocused(true);
        selectedItem.draggable_lists$setBeingDragged(true);
        softScrollingTimer = 0;
        return true;
    }

    private boolean captureDragCandidate(double mouseX, double mouseY) {
        DragItem<T, E> candidate = dragList.draggable_lists$getEntryAtPosition(mouseX, mouseY);
        if (candidate == null) return false;

        int entryIndex = dragList.draggable_lists$getIndexOfEntry(candidate);
        if (entryIndex < 0) return false;

        double offsetX = dragList.draggable_lists$getRowLeft() - mouseX;
        if (offsetX > -ICON_COLUMN_WIDTH) return false;

        dragCandidate = candidate;
        draggingStartX = mouseX;
        draggingStartY = mouseY;
        draggingOffsetX = offsetX;
        draggingOffsetY = dragList.draggable_lists$getRowTop(entryIndex) - mouseY;
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return selectedItem != null;
    }

    public void renderListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        int rowLeft = (int) dragList.draggable_lists$getRowLeft();
        int rowWidth = dragList.draggable_lists$getRowWidth();
        int rowHeight = dragList.draggable_lists$getItemHeight() - RENDER_MARGIN;
        int count = dragList.draggable_lists$getItemCount();

        int draggingIndex = selectedItem == null ? -1 : dragList.draggable_lists$getIndexOfEntry(selectedItem);
        int dropIndex = selectedItem == null ? -1 : getIndexFromMouseY(mouseY);

        for (int i = 0; i < count; i++) {
            int renderRow = shiftedIndex(i, draggingIndex, dropIndex);
            if (renderRow < 0 || renderRow >= count) continue;
            int rowTop = dragList.draggable_lists$getRowTop(renderRow);
            int rowBottom = dragList.draggable_lists$getRowBottom(renderRow);
            if (rowBottom >= dragList.draggable_lists$getY() && rowTop <= dragList.draggable_lists$getBottom() && i != draggingIndex) {
                dragList.draggable_lists$renderItem(guiGraphics, mouseX, mouseY, tickDelta, i, rowLeft, rowTop, rowWidth, rowHeight);
            }
        }
    }

    private int shiftedIndex(int i, int draggingIndex, int dropIndex) {
        if (i == draggingIndex) return dropIndex;
        if (draggingIndex < dropIndex) return i >= draggingIndex && i <= dropIndex ? i - 1 : i;
        return i >= dropIndex && i <= draggingIndex ? i + 1 : i;
    }

    public void renderWidget(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        if (selectedItem == null) return;

        int x = Mth.floor(draggingStartX + draggingOffsetX);
        int uncappedY = Mth.floor(mouseY + draggingOffsetY);
        int y = capItemY(uncappedY);
        int entryWidth = dragList.draggable_lists$getRowWidth();
        int entryHeight = dragList.draggable_lists$getItemHeight() - RENDER_MARGIN;

        float gray = 191f / 255f;
        guiGraphics.fill(x - 1, y - 1, x + entryWidth - 2, y + entryHeight + 1, ARGB.colorFromFloat(0.5f, gray, gray, gray));
        selectedItem.draggable_lists$render(guiGraphics, x, y, entryWidth, entryHeight, mouseX, mouseY, false, delta);
        guiGraphics.requestCursor(CursorTypes.RESIZE_NS);

        if (y == uncappedY) {
            softScrollingTimer = 0;
            return;
        }
        if (softScrollingTimer == 0) {
            softScrollingTimer = Util.getMillis();
            softScrollingOrigin = dragList.draggable_lists$getScrollAmount();
        }
        float scroll = (float) (Util.getMillis() - softScrollingTimer) * SOFT_SCROLL_PIXELS_PER_MS;
        dragList.draggable_lists$setScrollAmount(softScrollingOrigin + (y < uncappedY ? scroll : -scroll));
    }

    private int capMouseY(int y) {
        int top = dragList.draggable_lists$getY() + DRAG_AREA_TOP_INSET;
        int bottom = dragList.draggable_lists$getBottom() - DRAG_AREA_BOTTOM_INSET;
        return Math.max(top, Math.min(y, bottom));
    }

    private int capItemY(int y) {
        int maxItemTop = dragList.draggable_lists$getBottom() - DRAG_AREA_BOTTOM_INSET - (dragList.draggable_lists$getItemHeight() - RENDER_MARGIN);
        return Math.min(capMouseY(y), maxItemTop);
    }

    private boolean isMouseYInDragArea(int y) {
        return capMouseY(y) == y;
    }

    private int getIndexFromMouseY(double mouseY) {
        int count = dragList.draggable_lists$getItemCount();
        for (int i = 0; i < count; i++) {
            if (mouseY < dragList.draggable_lists$getRowBottom(i)) return i;
        }
        return count;
    }

    public static void renderEntryAt(ObjectSelectionList.Entry<?> entry, int x, int y, int width, int height, int baseWidth, Runnable extract) {
        int oldX = entry.getX();
        int oldY = entry.getY();
        int oldHeight = entry.getHeight();
        entry.setX(x);
        entry.setY(y);
        entry.setWidth(width);
        entry.setHeight(height);
        extract.run();
        entry.setX(oldX);
        entry.setY(oldY);
        entry.setWidth(baseWidth);
        entry.setHeight(oldHeight);
    }
}
