package dev.jafaar.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Recorder settings UI. This screen deliberately does not pretend to record:
 * the frame/audio capture and MP4 encoding backend is a separate implementation stage.
 */
public final class JafaarRecorderScreen extends Screen {
    private boolean gameAudio = true;
    private boolean microphone = false;
    private int quality = 1;
    private int frameRate = 1;

    public JafaarRecorderScreen() {
        super(Component.literal("JAFAAR RECORDER"));
    }

    @Override
    protected void init() {
        int panelWidth = 310;
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(42, (this.height - 190) / 2);

        this.addRenderableWidget(Button.builder(audioLabel(), button -> {
            gameAudio = !gameAudio;
            button.setMessage(audioLabel());
        }).bounds(left + 18, top + 36, panelWidth - 36, 20).build());

        this.addRenderableWidget(Button.builder(micLabel(), button -> {
            microphone = !microphone;
            button.setMessage(micLabel());
        }).bounds(left + 18, top + 61, panelWidth - 36, 20).build());

        this.addRenderableWidget(Button.builder(qualityLabel(), button -> {
            quality = (quality + 1) % 3;
            button.setMessage(qualityLabel());
        }).bounds(left + 18, top + 96, 132, 20).build());

        this.addRenderableWidget(Button.builder(fpsLabel(), button -> {
            frameRate = (frameRate + 1) % 2;
            button.setMessage(fpsLabel());
        }).bounds(left + 168, top + 96, 124, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Back"),
                button -> this.onClose())
                .bounds(left + 18, top + 151, panelWidth - 36, 20).build());
    }

    private Component audioLabel() {
        return Component.literal("Game audio: " + (gameAudio ? "ON" : "OFF"));
    }

    private Component micLabel() {
        return Component.literal("Microphone: " + (microphone ? "ON" : "OFF"));
    }

    private Component qualityLabel() {
        String[] values = {"720p", "1080p", "1440p"};
        return Component.literal("Quality: " + values[quality]);
    }

    private Component fpsLabel() {
        return Component.literal("FPS: " + (frameRate == 0 ? "30" : "60"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int panelWidth = 310;
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(42, (this.height - 190) / 2);
        graphics.fill(0, 0, this.width, this.height, 0xB8050910);
        graphics.fill(left, top, left + panelWidth, top + 190, 0xF0121825);
        graphics.fill(left, top, left + panelWidth, top + 3, 0xFFFF4545);
        graphics.outline(left, top, panelWidth, 190, 0xFF8C3540);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, Component.literal("JAFAAR RECORDER"),
                this.width / 2, top + 12, 0xFFFF6666);
        graphics.centeredText(this.font, Component.literal("VIDEO + AUDIO SETTINGS"),
                this.width / 2, top + 25, 0xFFBFC6D4);
        graphics.centeredText(this.font, Component.literal("Capture engine: NOT IMPLEMENTED YET"),
                this.width / 2, top + 128, 0xFFFFB36B);
        graphics.centeredText(this.font, Component.literal("Settings are a UI preview; no recording is made."),
                this.width / 2, top + 140, 0xFF9BA6B8);
    }
}
