package com.mrmelon54.DraggableLists;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;

public interface DragItem<T, E extends ObjectSelectionList.Entry<?>> {
    T draggable_lists$getUnderlyingData();

    E draggable_lists$getUnderlyingEntry();

    void draggable_lists$render(GuiGraphicsExtractor guiGraphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

    void draggable_lists$setBeingDragged(boolean v);
}
