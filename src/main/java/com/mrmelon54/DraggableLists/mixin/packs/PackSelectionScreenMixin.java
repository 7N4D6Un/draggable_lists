package com.mrmelon54.DraggableLists.mixin.packs;

import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackSelectionScreen.class)
public abstract class PackSelectionScreenMixin {
    @Shadow
    private TransferableSelectionList selectedPackList;

    @Shadow
    private TransferableSelectionList availablePackList;

    @Inject(method = "onClose", at = @At("HEAD"))
    private void draggable_lists$onClose(CallbackInfo ci) {
        if (selectedPackList.isDragging()) selectedPackList.mouseReleased(new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0)));
        if (availablePackList.isDragging()) availablePackList.mouseReleased(new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0)));
    }
}
