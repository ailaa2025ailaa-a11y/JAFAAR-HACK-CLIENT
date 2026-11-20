package dev.jafaar.client.module;

import java.util.Objects;

public final class ClientModule {
    private final String id;
    private final String displayName;
    private final ModuleCategory category;
    private boolean enabled;

    public ClientModule(String id, String displayName, ModuleCategory category, boolean defaultEnabled) {
        this.id = Objects.requireNonNull(id);
        this.displayName = Objects.requireNonNull(displayName);
        this.category = Objects.requireNonNull(category);
        this.enabled = defaultEnabled;
    }

    public String id() { return id; }
    public String displayName() { return displayName; }
    public ModuleCategory category() { return category; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void toggle() { enabled = !enabled; }
}
