package dev.jafaar.client.gui;

import dev.jafaar.client.hud.ClientHud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class JafaarHudEditorScreen extends Screen {
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public JafaarHudEditorScreen() { super(Component.literal("JAFAAR HUD EDITOR")); }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.literal("Reset position"), button ->
                ClientHud.setCoordinatesPosition(8, 8))
                .bounds(this.width / 2 - 105, this.height - 54, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Done"), button -> this.onClose())
                .bounds(this.width / 2 + 5, this.height - 54, 100, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, 0xB9080C14);
        graphics.centeredText(this.font, Component.literal("JAFAAR CLIENT  /  HUD EDITOR"),
                this.width / 2, 24, 0xFFFFC857);
        graphics.centeredText(this.font, Component.literal("Drag the coordinates panel to move it."),
                this.width / 2, 42, 0xFFE0E5EF);
        int x = ClientHud.getCoordinatesX();
        int y = ClientHud.getCoordinatesY();
        graphics.fill(x, y, x + 150, y + 28, 0xE51A2030);
        graphics.fill(x, y, x + 150, y + 3, 0xFFFFC857);
        graphics.outline(x, y, 150, 28, 0xFFFFC857);
        graphics.text(this.font, Component.literal("XYZ  120.5 / 64.0 / -32.5"),
                x + 7, y + 10, 0xFFFFFFFF, true);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int x = ClientHud.getCoordinatesX();
        int y = ClientHud.getCoordinatesY();
        if (event.button() == 0 && event.x() >= x && event.x() <= x + 150
                && event.y() >= y && event.y() <= y + 28) {
            dragging = true;
            dragOffsetX = (int) event.x() - x;
            dragOffsetY = (int) event.y() - y;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dragging && event.button() == 0) {
            ClientHud.setCoordinatesPosition((int) event.x() - dragOffsetX,
                    (int) event.y() - dragOffsetY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) dragging = false;
        return super.mouseReleased(event);
    }
}
