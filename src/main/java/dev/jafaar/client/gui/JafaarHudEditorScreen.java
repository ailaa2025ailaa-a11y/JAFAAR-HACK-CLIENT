package dev.jafaar.client.gui;

import dev.jafaar.client.hud.ClientHud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class JafaarHudEditorScreen extends Screen {
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public JafaarHudEditorScreen() {
        super(Component.literal("JAFAAR HUD EDITOR"));
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.literal("Reset position"), button -> {
            ClientHud.setCoordinatesPosition(8, 8);
        }).bounds(this.width / 2 - 105, this.height - 54, 100, 20).build());
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
        graphics.fill(x, y, x + 112, y + 48, 0xE51A2030);
        graphics.fill(x, y, x + 112, y + 3, 0xFFFFC857);
        graphics.outline(x, y, 112, 48, 0xFFFFC857);
        graphics.text(this.font, Component.literal("X: 120.5  Y: 64.0  Z: -32.5"), x + 7, y + 10, 0xFFFFFFFF, true);
        graphics.text(this.font, Component.literal("Coordinates HUD"), x + 7, y + 25, 0xFFFFD36A, true);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = ClientHud.getCoordinatesX();
        int y = ClientHud.getCoordinatesY();
        if (button == 0 && mouseX >= x && mouseX <= x + 112 && mouseY >= y && mouseY <= y + 48) {
            dragging = true;
            dragOffsetX = (int) mouseX - x;
            dragOffsetY = (int) mouseY - y;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && button == 0) {
            ClientHud.setCoordinatesPosition((int) mouseX - dragOffsetX, (int) mouseY - dragOffsetY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
