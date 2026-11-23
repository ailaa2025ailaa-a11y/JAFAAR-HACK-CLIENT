package dev.jafaar.client.module;

import dev.jafaar.client.config.ClientConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Modules {
    private static final Map<String, Module> MODULES = new LinkedHashMap<>();

    private Modules() {}

    public static void init() {
        MODULES.clear();
        add("fullbright", "Fullbright", Category.VISUALS, true);
        add("auto_sprint", "Auto Sprint", Category.MOVEMENT, false);
        add("coordinates", "Coordinates HUD", Category.HUD, true);
        add("fps", "FPS HUD", Category.HUD, true);
        add("keystrokes", "Keystrokes HUD", Category.HUD, false);
        for (Module module : MODULES.values()) {
            module.enabled = ClientConfig.get(module.id, module.enabled);
        }
    }

    private static void add(String id, String name, Category category, boolean enabled) {
        MODULES.put(id, new Module(id, name, category, enabled));
    }

    public static boolean enabled(String id) {
        Module module = MODULES.get(id);
        return module != null && module.enabled;
    }

    public static void toggle(String id) {
        Module module = MODULES.get(id);
        if (module != null) {
            module.enabled = !module.enabled;
            ClientConfig.set(module.id, module.enabled);
            ClientConfig.save();
        }
    }

    public static Map<String, Module> all() {
        return MODULES;
    }

    public enum Category { VISUALS, MOVEMENT, HUD }

    public static final class Module {
        public final String id;
        public final String name;
        public final Category category;
        public boolean enabled;

        private Module(String id, String name, Category category, boolean enabled) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.enabled = enabled;
        }
    }
}
