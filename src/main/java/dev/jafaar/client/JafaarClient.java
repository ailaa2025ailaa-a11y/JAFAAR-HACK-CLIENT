package dev.jafaar.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.jafaar.client.config.ClientConfig;
import dev.jafaar.client.gui.JafaarClickScreen;
import dev.jafaar.client.gui.JafaarMainMenuScreen;
import dev.jafaar.client.hud.ClientHud;
import dev.jafaar.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.logging.Logger;

public final class JafaarClient implements ClientModInitializer {
    public static final String MOD_ID = "jafaarclient";
    public static final Logger LOGGER = Logger.getLogger("JAFAAR CLIENT");
    private static ModuleManager modules;
    private static ClientConfig config;
    private static KeyMapping clickGuiKey;

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

        ClientHud.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (clickGuiKey.consumeClick()) {
                if (client.player != null) client.setScreen(new JafaarClickScreen());
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                screen.addRenderableWidget(Button.builder(Component.literal("JAFAAR MENU"),
                        button -> client.setScreen(new JafaarMainMenuScreen(screen)))
                        .bounds(scaledWidth - 112, 8, 104, 20).build());
            }
        });

        LOGGER.info("JAFAAR CLIENT initialized with " + modules.getModules().size() + " working HUD modules.");
    }

    public static ModuleManager modules() { return modules; }
    public static ClientConfig config() { return config; }
}
