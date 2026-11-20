package dev.jafaar.client;

import dev.jafaar.client.config.ClientConfig;
import dev.jafaar.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.logging.Logger;

public final class JafaarClient implements ClientModInitializer {
    public static final String MOD_ID = "jafaarclient";
    public static final Logger LOGGER = Logger.getLogger("JAFAAR CLIENT");

    private static ModuleManager modules;
    private static ClientConfig config;

    @Override
    public void onInitializeClient() {
        config = new ClientConfig(FabricLoader.getInstance().getConfigDir().resolve("jafaar-client.properties"));
        modules = new ModuleManager();
        modules.registerDefaults();
        config.load(modules);
        LOGGER.info("JAFAAR CLIENT initialized. Registered " + modules.getModules().size() + " client modules.");
    }

    public static ModuleManager modules() {
        return modules;
    }

    public static ClientConfig config() {
        return config;
    }
}
