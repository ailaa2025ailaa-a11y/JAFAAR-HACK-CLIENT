package dev.jafaar.client.module;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ModuleManager {
    private final Map<String, ClientModule> modules = new LinkedHashMap<>();

    public void register(ClientModule module) {
        if (modules.putIfAbsent(module.id(), module) != null) {
            throw new IllegalArgumentException("Duplicate module id: " + module.id());
        }
    }

    public void registerDefaults() {
        register(new ClientModule("fps_profile", "FPS Profile", ModuleCategory.PERFORMANCE, false));
        register(new ClientModule("reduced_visual_effects", "Reduced Visual Effects", ModuleCategory.PERFORMANCE, false));
        register(new ClientModule("fullbright", "Fullbright", ModuleCategory.VISUAL, false));
        register(new ClientModule("coordinates_hud", "Coordinates HUD", ModuleCategory.HUD, true));
        register(new ClientModule("keystrokes_hud", "Keystrokes HUD", ModuleCategory.HUD, false));
        register(new ClientModule("cps_counter", "CPS Counter", ModuleCategory.HUD, false));
        register(new ClientModule("zoom", "Zoom", ModuleCategory.UTILITY, false));
        register(new ClientModule("toggle_sprint", "Toggle Sprint", ModuleCategory.MOVEMENT, false));
    }

    public Optional<ClientModule> get(String id) {
        return Optional.ofNullable(modules.get(id));
    }

    public Collection<ClientModule> getModules() {
        return Collections.unmodifiableCollection(modules.values());
    }
}
