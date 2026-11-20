package dev.jafaar.client.module;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ModuleManager {
    private final Map<String, ClientModule> modules = new LinkedHashMap<>();
    public void register(ClientModule module) {
        if (modules.putIfAbsent(module.id(), module) != null)
            throw new IllegalArgumentException("Duplicate module id: " + module.id());
    }
    public void registerDefaults() {
        register(new ClientModule("custom_crosshair", "Custom Crosshair", ModuleCategory.VISUAL, true));
        register(new ClientModule("fps_hud", "FPS Counter", ModuleCategory.HUD, true));
        register(new ClientModule("memory_hud", "Memory Monitor", ModuleCategory.HUD, false));
        register(new ClientModule("coordinates_hud", "Coordinates HUD", ModuleCategory.HUD, true));
        register(new ClientModule("direction_hud", "Direction HUD", ModuleCategory.HUD, false));
        register(new ClientModule("keystrokes_hud", "Keystrokes HUD", ModuleCategory.HUD, false));
        register(new ClientModule("vitals_hud", "Health and Food HUD", ModuleCategory.HUD, false));
        register(new ClientModule("world_clock_hud", "World Clock HUD", ModuleCategory.HUD, false));
        register(new ClientModule("movement_status_hud", "Movement Status HUD", ModuleCategory.HUD, false));
    }
    public Optional<ClientModule> get(String id) { return Optional.ofNullable(modules.get(id)); }
    public Collection<ClientModule> getModules() { return Collections.unmodifiableCollection(modules.values()); }
}
