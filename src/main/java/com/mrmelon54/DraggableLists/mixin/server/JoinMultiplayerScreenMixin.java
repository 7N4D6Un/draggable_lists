package com.mrmelon54.DraggableLists.mixin.server;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen {
    @Shadow
    protected ServerSelectionList serverSelectionList;

    protected JoinMultiplayerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "join", at = @At("HEAD"))
    private void draggable_lists$injectedConnect(ServerData serverData, CallbackInfo info) {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0)));
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(event);
        return super.mouseReleased(event);
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void draggable_lists$onClose(CallbackInfo ci) {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0)));
    }
}
