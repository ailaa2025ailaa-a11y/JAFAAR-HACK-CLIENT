package dev.jafaar.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.jafaar.client.config.ClientConfig;
import dev.jafaar.client.gui.ClickGuiScreen;
import dev.jafaar.client.hud.ClientHud;
import dev.jafaar.client.module.Modules;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public final class JafaarClient implements ClientModInitializer {
    public static final String MOD_ID = "jafaarclient";
    private static KeyMapping clickGuiKey;
    private static KeyMapping zoomKey;
    private static double previousGamma = 1.0;
    private static double previousFov = 70.0;
    private static boolean gammaCaptured;
    private static boolean zooming;

    @Override
    public void onInitializeClient() {
        ClientConfig.load();
        Modules.init();

        KeyMapping.Category category = KeyMapping.Category.register(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"));

        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.jafaarclient.click_gui", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.jafaarclient.zoom", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C, category));

        ClientHud.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                restoreZoom(client);
                return;
            }

            while (clickGuiKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }

            boolean fullbright = Modules.enabled("fullbright");
            if (fullbright) {
                if (!gammaCaptured) {
                    previousGamma = client.options.gamma().get();
                    gammaCaptured = true;
                }
                client.options.gamma().set(16.0);
            } else if (gammaCaptured) {
                client.options.gamma().set(previousGamma);
                gammaCaptured = false;
            }

            if (Modules.enabled("auto_sprint")
                    && client.player.input.hasForwardImpulse()
                    && !client.player.isCrouching()
                    && !client.player.isPassenger()) {
                client.player.setSprinting(true);
            }

            if (zoomKey.isDown() && client.screen == null) {
                if (!zooming) {
                    previousFov = client.options.fov().get();
                    zooming = true;
                }
                client.options.fov().set(25);
            } else {
                restoreZoom(client);
            }
        });
    }

    private static void restoreZoom(net.minecraft.client.Minecraft client) {
        if (zooming) {
            client.options.fov().set((int) previousFov);
            zooming = false;
        }
    }
}
