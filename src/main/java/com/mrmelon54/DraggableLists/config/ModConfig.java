package com.mrmelon54.DraggableLists.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("draggable_lists.json");

    public static boolean disableResourcePackArrows = true;
    public static boolean disableServerArrows = true;
    public static EnabledMode resourcePackDraggingEnabled = EnabledMode.ENABLED;
    public static EnabledMode serverDraggingEnabled = EnabledMode.ENABLED;

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Data data = GSON.fromJson(reader, Data.class);
                if (data != null) {
                    disableResourcePackArrows = data.disableResourcePackArrows;
                    disableServerArrows = data.disableServerArrows;
                    resourcePackDraggingEnabled = data.resourcePackDraggingEnabled;
                    serverDraggingEnabled = data.serverDraggingEnabled;
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        saveDefaults();
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                Data data = new Data();
                data.disableResourcePackArrows = disableResourcePackArrows;
                data.disableServerArrows = disableServerArrows;
                data.resourcePackDraggingEnabled = resourcePackDraggingEnabled;
                data.serverDraggingEnabled = serverDraggingEnabled;
                GSON.toJson(data, writer);
            }
        } catch (IOException ignored) {
        }
    }

    private static void saveDefaults() {
        disableResourcePackArrows = true;
        disableServerArrows = true;
        resourcePackDraggingEnabled = EnabledMode.ENABLED;
        serverDraggingEnabled = EnabledMode.ENABLED;
        save();
    }

    private static class Data {
        boolean disableResourcePackArrows = true;
        boolean disableServerArrows = true;
        EnabledMode resourcePackDraggingEnabled = EnabledMode.ENABLED;
        EnabledMode serverDraggingEnabled = EnabledMode.ENABLED;
    }
}
