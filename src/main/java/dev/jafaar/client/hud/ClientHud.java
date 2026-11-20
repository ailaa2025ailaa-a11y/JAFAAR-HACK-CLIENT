package dev.jafaar.client.hud;

import dev.jafaar.client.JafaarClient;
import dev.jafaar.client.module.ClientModule;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public final class ClientHud {
    private ClientHud() {
    }

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(JafaarClient.MOD_ID, "coordinates"),
                (graphics, deltaTracker) -> {
                    ClientModule module = JafaarClient.modules().get("coordinates_hud").orElse(null);
                    if (module == null || !module.isEnabled()) {
                        return;
                    }

                    Minecraft client = Minecraft.getInstance();
                    if (client.player == null) {
                        return;
                    }

                    String x = String.format(Locale.ROOT, "X: %.1f", client.player.getX());
                    String y = String.format(Locale.ROOT, "Y: %.1f", client.player.getY());
                    String z = String.format(Locale.ROOT, "Z: %.1f", client.player.getZ());

                    graphics.fill(6, 6, 94, 49, 0xB8101420);
                    graphics.fill(6, 6, 94, 8, 0xFFFFC857);
                    graphics.text(client.font, Component.literal(x), 12, 14, 0xFFFFFFFF, true);
                    graphics.text(client.font, Component.literal(y), 12, 25, 0xFFFFFFFF, true);
                    graphics.text(client.font, Component.literal(z), 12, 36, 0xFFFFFFFF, true);
                }
        );
    }
}
