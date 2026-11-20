package dev.jafaar.client.hud;

import dev.jafaar.client.JafaarClient;
import dev.jafaar.client.module.ClientModule;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class ClientHud {
    private static int coordinatesX = 8;
    private static int coordinatesY = 8;

    private ClientHud() {}

    public static void setCoordinatesPosition(int x, int y) {
        coordinatesX = Math.max(0, x);
        coordinatesY = Math.max(0, y);
    }
    public static int getCoordinatesX() { return coordinatesX; }
    public static int getCoordinatesY() { return coordinatesY; }

    public static void register() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(JafaarClient.MOD_ID, "client_hud"),
                (graphics, deltaTracker) -> {
                    Minecraft client = Minecraft.getInstance();
                    if (client.player == null) return;

                    if (enabled("coordinates_hud")) {
                        String coords = String.format(java.util.Locale.ROOT, "XYZ  %.1f / %.1f / %.1f",
                                client.player.getX(), client.player.getY(), client.player.getZ());
                        panel(graphics, coordinatesX, coordinatesY, 150, 25);
                        graphics.text(client.font, Component.literal(coords), coordinatesX + 7,
                                coordinatesY + 9, 0xFFFFFFFF, true);
                    }
                    if (enabled("fps_hud")) {
                        panel(graphics, 8, 40, 92, 23);
                        graphics.text(client.font, Component.literal("FPS  " + client.getFps()),
                                15, 48, 0xFFFFFFFF, true);
                    }
                    if (enabled("keystrokes_hud")) {
                        int x = client.getWindow().getGuiScaledWidth() - 82;
                        int y = client.getWindow().getGuiScaledHeight() - 78;
                        key(graphics, client, "W", client.options.keyUp.isDown(), x + 28, y, 26);
                        key(graphics, client, "A", client.options.keyLeft.isDown(), x, y + 25, 26);
                        key(graphics, client, "S", client.options.keyDown.isDown(), x + 28, y + 25, 26);
                        key(graphics, client, "D", client.options.keyRight.isDown(), x + 56, y + 25, 26);
                        key(graphics, client, "JUMP", client.options.keyJump.isDown(), x, y + 50, 82);
                    }
                });
    }

    private static boolean enabled(String id) {
        ClientModule module = JafaarClient.modules().get(id).orElse(null);
        return module != null && module.isEnabled();
    }
    private static void panel(net.minecraft.client.gui.GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xC9101420);
        g.fill(x, y, x + w, y + 2, 0xFFFFC857);
        g.outline(x, y, w, h, 0x995E6575);
    }
    private static void key(net.minecraft.client.gui.GuiGraphicsExtractor g, Minecraft c,
                            String label, boolean down, int x, int y, int width) {
        g.fill(x, y, x + width, y + 22, down ? 0xFFE4A93B : 0xC91A2030);
        g.outline(x, y, width, 22, 0xFFFFC857);
        g.centeredText(c.font, Component.literal(label), x + width / 2, y + 7, 0xFFFFFFFF);
    }
}
