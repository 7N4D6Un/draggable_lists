package com.mrmelon54.DraggableLists.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class DraggableListsConfigScreen {
    private DraggableListsConfigScreen() {
    }

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("draggable_lists.config.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(
                Component.translatable("draggable_lists.config.category.general"));

        general.addEntry(entryBuilder
                .startBooleanToggle(
                        Component.translatable("draggable_lists.config.disable_resource_pack_arrows"),
                        ModConfig.disableResourcePackArrows)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("draggable_lists.config.disable_resource_pack_arrows.tooltip"))
                .setSaveConsumer(v -> ModConfig.disableResourcePackArrows = v)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(
                        Component.translatable("draggable_lists.config.disable_server_arrows"),
                        ModConfig.disableServerArrows)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("draggable_lists.config.disable_server_arrows.tooltip"))
                .setSaveConsumer(v -> ModConfig.disableServerArrows = v)
                .build());

        general.addEntry(entryBuilder
                .startEnumSelector(
                        Component.translatable("draggable_lists.config.resource_pack_dragging"),
                        EnabledMode.class,
                        ModConfig.resourcePackDraggingEnabled)
                .setDefaultValue(EnabledMode.ENABLED)
                .setTooltip(Component.translatable("draggable_lists.config.resource_pack_dragging.tooltip"))
                .setSaveConsumer(v -> ModConfig.resourcePackDraggingEnabled = v)
                .build());

        general.addEntry(entryBuilder
                .startEnumSelector(
                        Component.translatable("draggable_lists.config.server_dragging"),
                        EnabledMode.class,
                        ModConfig.serverDraggingEnabled)
                .setDefaultValue(EnabledMode.ENABLED)
                .setTooltip(Component.translatable("draggable_lists.config.server_dragging.tooltip"))
                .setSaveConsumer(v -> ModConfig.serverDraggingEnabled = v)
                .build());

        builder.setSavingRunnable(ModConfig::save);
        return builder.build();
    }
}
