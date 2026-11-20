package dev.jafaar.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.jafaar.client.config.ClientConfig;
import dev.jafaar.client.gui.JafaarClickScreen;
import dev.jafaar.client.gui.JafaarMainMenuScreen;
import dev.jafaar.client.hud.ClientHud;
import dev.jafaar.client.module.ModuleManager;
import dev.jafaar.client.recorder.JafaarRecorder;\nimport dev.jafaar.client.recorder.JafaarMicrophoneCapture;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.logging.Logger;

public final class JafaarClient implements ClientModInitializer {
    public static final String MOD_ID = "jafaarclient";
    public static final Logger LOGGER = Logger.getLogger("JAFAAR CLIENT");
    private static ModuleManager modules;
    private static ClientConfig config;
    private static KeyMapping clickGuiKey;
    private static KeyMapping customMenuKey;

    @Override
    public void onInitializeClient() {
        config = new ClientConfig(FabricLoader.getInstance().getConfigDir().resolve("jafaar-client.properties"));
        modules = new ModuleManager();
        modules.registerDefaults();
        config.load(modules);

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));
        clickGuiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.jafaarclient.click_gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        customMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.jafaarclient.custom_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F8, category));

        ClientHud.register();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {\n            JafaarRecorder.stop();\n            JafaarMicrophoneCapture.stop();\n        }, "JAFAAR-Recorder-Shutdown"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            JafaarRecorder.captureFrame(client);
            while (clickGuiKey.consumeClick()) {
                if (client.player != null) client.setScreen(new JafaarClickScreen());
            }
            while (customMenuKey.consumeClick()) {
                if (client.screen instanceof TitleScreen) {
                    client.setScreen(new JafaarMainMenuScreen(client.screen));
                }
            }
        });

        LOGGER.info("JAFAAR CLIENT initialized with " + modules.getModules().size() + " working HUD modules.");
    }

    public static ModuleManager modules() { return modules; }
    public static ClientConfig config() { return config; }
}
