package dev.jafaar.client.gui;

import dev.jafaar.client.JafaarClient;
import dev.jafaar.client.module.ClientModule;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class JafaarClickScreen extends Screen {
    private static final int PANEL_WIDTH = 430;
    private static final int BUTTON_WIDTH = 190;
    private static final int BUTTON_HEIGHT = 20;

    public JafaarClickScreen() {
        super(Component.literal("JAFAAR CLIENT"));
    }

    @Override
    protected void init() {
        List<ClientModule> modules = new ArrayList<>(JafaarClient.modules().getModules());
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(54, (this.height - 190) / 2);

        for (int i = 0; i < modules.size(); i++) {
            ClientModule module = modules.get(i);
            int column = i / 5;
            int row = i % 5;
            int x = left + 12 + column * 207;
            int y = top + row * 27;
            Button toggle = Button.builder(moduleLabel(module), button -> {
                module.toggle();
                button.setMessage(moduleLabel(module));
                JafaarClient.config().save(JafaarClient.modules());
            }).bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).build();
            this.addRenderableWidget(toggle);
        }

        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(this.width / 2 - 70, Math.min(this.height - 34, top + 5 * 27 + 8), 140, BUTTON_HEIGHT)
                .build());
    }

    private static Component moduleLabel(ClientModule module) {
        return Component.literal((module.isEnabled() ? "[ON] " : "[OFF] ") + module.displayName());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(54, (this.height - 190) / 2);
        graphics.fill(left, top - 35, left + PANEL_WIDTH, top + 5 * 27 + 34, 0xE51A1D27);
        graphics.outline(left, top - 35, PANEL_WIDTH, 5 * 27 + 69, 0xFFB58A32);
        graphics.centeredText(this.font, Component.literal("JAFAAR CLIENT"), this.width / 2, top - 27, 0xFFFFD36A);
        graphics.centeredText(this.font, Component.literal("Right Shift: open menu  |  Click a module to toggle"),
                this.width / 2, top + 5 * 27 + 17, 0xFFBFC6D4);
    }
}
