package dev.jafaar.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class JafaarMainMenuScreen extends Screen {
    private final Screen parent;

    public JafaarMainMenuScreen(Screen parent) {
        super(Component.literal("JAFAAR CLIENT"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int top = this.height / 2 - 38;
        this.addRenderableWidget(Button.builder(Component.literal("Open Client GUI"),
                button -> this.minecraft.setScreen(new JafaarClickScreen()))
                .bounds(center - 105, top, 210, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("HUD Editor"),
                button -> this.minecraft.setScreen(new JafaarHudEditorScreen()))
                .bounds(center - 105, top + 27, 210, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Return to Minecraft Menu"),
                button -> this.minecraft.setScreen(parent))
                .bounds(center - 105, top + 54, 210, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, 0xFF080C14);
        graphics.fill(0, 0, this.width, 4, 0xFFFFC857);
        graphics.fill(this.width / 2 - 150, this.height / 2 - 105,
                this.width / 2 + 150, this.height / 2 + 92, 0xE9121825);
        graphics.outline(this.width / 2 - 150, this.height / 2 - 105,
                300, 197, 0xFF8C6B32);
        graphics.centeredText(this.font, Component.literal("JAFAAR CLIENT"),
                this.width / 2, this.height / 2 - 91, 0xFFFFC857);
        graphics.centeredText(this.font, Component.literal("PVP  •  PERFORMANCE  •  CUSTOMIZATION"),
                this.width / 2, this.height / 2 - 73, 0xFFE0E5EF);
        graphics.centeredText(this.font, Component.literal("26.1  |  Fabric Edition"),
                this.width / 2, this.height - 24, 0xFF8E9AAF);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }
}
