package dev.jafaar.client.gui;

import dev.jafaar.client.module.Modules;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class ClickGuiScreen extends Screen {
    private static final int PANEL_WIDTH = 360;

    public ClickGuiScreen() {
        super(Component.literal("JAFAAR HACK CLIENT"));
    }

    @Override
    protected void init() {
        List<Modules.Module> modules = new ArrayList<>(Modules.all().values());
        int left = (width - PANEL_WIDTH) / 2;
        int top = Math.max(54, (height - Math.min(300, modules.size() * 30 + 60)) / 2);

        for (int i = 0; i < modules.size(); i++) {
            Modules.Module module = modules.get(i);
            int y = top + 44 + i * 30;
            addRenderableWidget(Button.builder(label(module), button -> {
                Modules.toggle(module.id);
                button.setMessage(label(module));
            }).bounds(left + 20, y, PANEL_WIDTH - 40, 22).build());
        }

        addRenderableWidget(Button.builder(Component.literal("CLOSE"), button -> onClose())
                .bounds(left + 20, Math.min(height - 30, top + 52 + modules.size() * 30),
                        PANEL_WIDTH - 40, 20).build());
    }

    private static Component label(Modules.Module module) {
        return Component.literal((module.enabled ? "ON  " : "OFF ") + module.name
                + "   [" + module.category.name() + "]");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        int left = (width - PANEL_WIDTH) / 2;
        int top = Math.max(54, (height - 280) / 2);
        graphics.fill(left, top, left + PANEL_WIDTH, Math.min(height - 12, top + 300), 0xF0121724);
        graphics.fill(left, top, left + PANEL_WIDTH, top + 3, 0xFFFFC857);
        graphics.drawCenteredString(font, "JAFAAR HACK CLIENT", width / 2, top + 12, 0xFFFFD36A);
        graphics.drawCenteredString(font, "RIGHT SHIFT  •  MODULE CONTROL", width / 2, top + 27, 0xFFB9C2D0);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
