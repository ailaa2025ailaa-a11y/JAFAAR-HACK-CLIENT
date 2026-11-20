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
    private static final int PANEL_WIDTH = 360;
    public JafaarClickScreen() { super(Component.literal("JAFAAR CLIENT")); }

    @Override
    protected void init() {
        List<ClientModule> modules = new ArrayList<>(JafaarClient.modules().getModules());
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(72, (this.height - 190) / 2);
        for (int i = 0; i < modules.size(); i++) {
            ClientModule module = modules.get(i);
            Button toggle = Button.builder(moduleLabel(module), button -> {
                module.toggle();
                button.setMessage(moduleLabel(module));
                JafaarClient.config().save(JafaarClient.modules());
            }).bounds(left + 20, top + i * 29, PANEL_WIDTH - 40, 22).build();
            this.addRenderableWidget(toggle);
        }
        int bottom = Math.min(this.height - 32, top + modules.size() * 29 + 12);
        this.addRenderableWidget(Button.builder(Component.literal("HUD Editor"),
                button -> this.minecraft.setScreen(new JafaarHudEditorScreen()))
                .bounds(left + 12, bottom, 108, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Recorder"),
                button -> this.minecraft.setScreen(new JafaarRecorderScreen()))
                .bounds(left + 126, bottom, 108, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(left + 240, bottom, 108, 20).build());
    }

    private static Component moduleLabel(ClientModule module) {
        return Component.literal((module.isEnabled() ? "●  " : "○  ") + module.displayName());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(72, (this.height - 190) / 2);
        int height = Math.min(this.height - top - 16, 190);
        graphics.fill(0, 0, this.width, this.height, 0x99050910);
        graphics.fill(left, top - 42, left + PANEL_WIDTH, top + height, 0xF0121825);
        graphics.fill(left, top - 42, left + PANEL_WIDTH, top - 39, 0xFFFFC857);
        graphics.outline(left, top - 42, PANEL_WIDTH, height + 42, 0xFF8C6B32);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, Component.literal("JAFAAR CLIENT"),
                this.width / 2, top - 32, 0xFFFFD36A);
        graphics.centeredText(this.font, Component.literal("VISUALS  •  HUD  •  UTILITIES"),
                this.width / 2, top - 18, 0xFFBFC6D4);
        graphics.centeredText(this.font, Component.literal("Right Shift  /  Toggle modules and customize your HUD"),
                this.width / 2, this.height - 15, 0xFF9BA6B8);
    }
}
