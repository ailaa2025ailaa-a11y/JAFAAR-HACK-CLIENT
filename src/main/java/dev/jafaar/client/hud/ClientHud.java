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

                    if (enabled("custom_crosshair") && client.screen == null && client.options.getCameraType().isFirstPerson()) {
                        int cx = client.getWindow().getGuiScaledWidth() / 2;
                        int cy = client.getWindow().getGuiScaledHeight() / 2;
                        int color = 0xFFFFC857;
                        graphics.fill(cx - 1, cy - 7, cx + 2, cy - 2, color);
                        graphics.fill(cx - 1, cy + 2, cx + 2, cy + 7, color);
                        graphics.fill(cx - 7, cy - 1, cx - 2, cy + 2, color);
                        graphics.fill(cx + 2, cy - 1, cx + 7, cy + 2, color);
                        graphics.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFF202020);
                    }
                    if (enabled("coordinates_hud")) {
                        String coords = String.format(java.util.Locale.ROOT, "XYZ  %.1f / %.1f / %.1f",
                                client.player.getX(), client.player.getY(), client.player.getZ());
                        panel(graphics, coordinatesX, coordinatesY, 150, 25);
                        graphics.text(client.font, Component.literal(coords), coordinatesX + 7,
                                coordinatesY + 9, 0xFFFFFFFF, true);
                    }
                    if (enabled("fps_hud")) {
                        int fps = client.getFps();
                        int color = fps >= 60 ? 0xFF73E6A1 : fps >= 30 ? 0xFFFFD36A : 0xFFFF7777;
                        panel(graphics, 8, 40, 100, 23);
                        graphics.text(client.font, Component.literal("FPS  " + fps),
                                15, 48, color, true);
                    }
                    if (enabled("memory_hud")) {
                        Runtime runtime = Runtime.getRuntime();
                        long used = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
                        long max = runtime.maxMemory() / 1048576L;
                        panel(graphics, 8, 67, 150, 23);
                        graphics.text(client.font, Component.literal("MEM  " + used + " / " + max + " MB"),
                                15, 75, 0xFFFFFFFF, true);
                    }
                    if (enabled("direction_hud")) {
                        String[] directions = {"S", "W", "N", "E"};
                        int index = ((int) Math.floor(client.player.getYRot() / 90.0f + 0.5f) & 3);
                        panel(graphics, 8, 94, 92, 23);
                        graphics.text(client.font, Component.literal("FACING  " + directions[index]),
                                15, 102, 0xFFFFFFFF, true);
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
