package dev.jafaar.client.gui;

import dev.jafaar.client.recorder.JafaarRecorder;\nimport dev.jafaar.client.recorder.JafaarMicrophoneCapture;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class JafaarRecorderScreen extends Screen {
    private int quality = 0;

    public JafaarRecorderScreen() {
        super(Component.literal("JAFAAR RECORDER"));
    }

    @Override
    protected void init() {
        int panelWidth = 310;
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(35, (this.height - 205) / 2);

        this.addRenderableWidget(Button.builder(qualityLabel(), button -> {
            quality = (quality + 1) % 3;
            button.setMessage(qualityLabel());
        }).bounds(left + 18, top + 42, panelWidth - 36, 20).build());

        this.addRenderableWidget(Button.builder(micLabel(), button -> {\n            if (JafaarMicrophoneCapture.isRecording()) JafaarMicrophoneCapture.stop();\n            else JafaarMicrophoneCapture.start(net.minecraft.client.Minecraft.getInstance().gameDirectory.toPath().resolve("recordings"));\n            button.setMessage(micLabel());\n        }).bounds(left + 18, top + 76, panelWidth - 36, 20).build());\n\n        this.addRenderableWidget(Button.builder(recordLabel(), button -> {
            if (JafaarRecorder.isRecording()) {
                JafaarRecorder.stop();
            } else {
                int[] heights = {720, 1080, 1440};
                JafaarRecorder.start(heights[quality]);
            }
            button.setMessage(recordLabel());
        }).bounds(left + 18, top + 102, panelWidth - 36, 24).build());

        this.addRenderableWidget(Button.builder(Component.literal("Back"),
                button -> this.onClose())
                .bounds(left + 18, top + 169, panelWidth - 36, 20).build());
    }

    private Component qualityLabel() {
        String[] values = {"720p", "1080p", "1440p"};
        return Component.literal("Video quality: " + values[quality]);
    }

    private Component micLabel() {\n        return Component.literal(JafaarMicrophoneCapture.isRecording() ? "■  STOP MICROPHONE" : "●  START MICROPHONE");\n    }\n\n    private Component recordLabel() {
        return Component.literal(JafaarRecorder.isRecording() ? "■  STOP RECORDING" : "●  START RECORDING");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int panelWidth = 310;
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(35, (this.height - 205) / 2);
        graphics.fill(0, 0, this.width, this.height, 0xB8050910);
        graphics.fill(left, top, left + panelWidth, top + 205, 0xF0121825);
        graphics.fill(left, top, left + panelWidth, top + 3, 0xFFFF4545);
        graphics.outline(left, top, panelWidth, 205, 0xFF8C3540);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, Component.literal("JAFAAR RECORDER"),
                this.width / 2, top + 13, 0xFFFF6666);
        graphics.centeredText(this.font, Component.literal("LIVE GAME CAPTURE  •  20 FPS"),
                this.width / 2, top + 27, 0xFFBFC6D4);
        graphics.centeredText(this.font, Component.literal("Video only — audio is Phase 3"),
                this.width / 2, top + 111, 0xFFFFB36B);
        graphics.centeredText(this.font, Component.literal("Status: " + JafaarRecorder.status()),
                this.width / 2, top + 117, 0xFFE4E8EF);
        graphics.centeredText(this.font, Component.literal("Saved in: .minecraft/recordings"),
                this.width / 2, top + 166, 0xFF9BA6B8);
        graphics.centeredText(this.font, Component.literal("Dropped frames: " + JafaarRecorder.droppedFrames()),
                this.width / 2, top + 180, 0xFF9BA6B8);
    }
}
