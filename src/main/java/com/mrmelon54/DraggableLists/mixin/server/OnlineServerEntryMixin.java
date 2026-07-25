package com.mrmelon54.DraggableLists.mixin.server;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.config.ModConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class OnlineServerEntryMixin extends ObjectSelectionList.Entry<ServerSelectionList.Entry> implements DragItem<ServerData, ServerSelectionList.OnlineServerEntry> {
    @Shadow
    @Final
    private ServerData serverData;

    @Shadow
    public abstract void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a);

    @Shadow
    @Final
    ServerSelectionList this$0;

    @Shadow
    public abstract boolean mouseClicked(MouseButtonEvent event, boolean doubleClick);

    @Unique
    private boolean draggable_lists$isBeingDragged;

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 3))
    public boolean removeUpOnButton(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        return !ModConfig.disableServerArrows;
    }

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 4))
    public boolean removeUpButton(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        return !ModConfig.disableServerArrows;
    }

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 5))
    public boolean removeDownOnButton(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        return !ModConfig.disableServerArrows;
    }

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 6))
    public boolean removeDownButton(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        return !ModConfig.disableServerArrows;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void removeSwapEntries(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        double l = this$0.getRowLeft();
        double f = event.x() - l;

        // don't click buttons left of 16 pixels
        if (f <= 16) {
            mouseClicked(new MouseButtonEvent(l + 32, event.y(), event.buttonInfo()), doubleClick);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public ServerData draggable_lists$getUnderlyingData() {
        return serverData;
    }

    @Override
    public ServerSelectionList.OnlineServerEntry draggable_lists$getUnderlyingEntry() {
        return (ServerSelectionList.OnlineServerEntry) (Object) this;
    }

    @Override
    public void draggable_lists$render(GuiGraphicsExtractor guiGraphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (!draggable_lists$isBeingDragged) return;
        int oldX = getX();
        int oldY = getY();
        int oldWidth = getWidth();
        int oldHeight = getHeight();
        setX(x);
        setY(y);
        setWidth(entryWidth);
        setHeight(entryHeight);
        extractContent(guiGraphics, mouseX, mouseY, hovered, tickDelta);
        setX(oldX);
        setY(oldY);
        setWidth(oldWidth);
        setHeight(oldHeight);
    }

    @Override
    public void draggable_lists$setBeingDragged(boolean v) {
        draggable_lists$isBeingDragged = v;
    }
}
