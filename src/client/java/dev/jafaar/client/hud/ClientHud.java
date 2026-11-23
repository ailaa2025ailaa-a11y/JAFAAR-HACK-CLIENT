package dev.jafaar.client.hud;

import dev.jafaar.client.JafaarClient;
import dev.jafaar.client.module.Modules;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ClientHud {
    private ClientHud() {}

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                ResourceLocation.fromNamespaceAndPath(JafaarClient.MOD_ID, "client_hud"),
                ClientHud::render
        );
    }

    private static void render(GuiGraphics graphics, net.minecraft.client.DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) return;

        int y = 8;
        if (Modules.enabled("coordinates")) {
            String coords = String.format(java.util.Locale.ROOT, "XYZ  %.1f / %.1f / %.1f",
                    client.player.getX(), client.player.getY(), client.player.getZ());
            panel(graphics, 8, y, 156, 22);
            graphics.drawString(client.font, coords, 15, y + 7, 0xFFFFFFFF, true);
            y += 26;
        }

        if (Modules.enabled("fps")) {
            panel(graphics, 8, y, 90, 22);
            graphics.drawString(client.font, "FPS  " + client.getFps(), 15, y + 7, 0xFFFFD36A, true);
        }

        if (Modules.enabled("keystrokes")) {
            int x = client.getWindow().getGuiScaledWidth() - 84;
            int bottom = client.getWindow().getGuiScaledHeight() - 82;
            key(graphics, client, "W", client.options.keyUp.isDown(), x + 28, bottom);
            key(graphics, client, "A", client.options.keyLeft.isDown(), x, bottom + 25);
            key(graphics, client, "S", client.options.keyDown.isDown(), x + 28, bottom + 25);
            key(graphics, client, "D", client.options.keyRight.isDown(), x + 56, bottom + 25);
            key(graphics, client, "JUMP", client.options.keyJump.isDown(), x, bottom + 50, 82);
        }
    }

    private static void panel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xC9101420);
        graphics.fill(x, y, x + width, y + 2, 0xFFFFC857);
        graphics.renderOutline(x, y, width, height, 0x995E6575);
    }

    private static void key(GuiGraphics graphics, Minecraft client, String text,
                            boolean pressed, int x, int y) {
        key(graphics, client, text, pressed, x, y, 26);
    }

    private static void key(GuiGraphics graphics, Minecraft client, String text,
                            boolean pressed, int x, int y, int width) {
        graphics.fill(x, y, x + width, y + 22, pressed ? 0xFFE4A93B : 0xC91A2030);
        graphics.renderOutline(x, y, width, 22, 0xFFFFC857);
        graphics.drawCenteredString(client.font, Component.literal(text), x + width / 2, y + 7, 0xFFFFFFFF);
    }
}
