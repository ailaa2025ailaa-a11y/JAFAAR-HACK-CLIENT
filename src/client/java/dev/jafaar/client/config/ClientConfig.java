package dev.jafaar.client.config;

import dev.jafaar.client.JafaarClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ClientConfig {
    private static final Properties VALUES = new Properties();
    private static final Path FILE = FabricConfigPath.path();

    private ClientConfig() {}

    public static void load() {
        if (Files.exists(FILE)) {
            try (InputStream input = Files.newInputStream(FILE)) {
                VALUES.load(input);
            } catch (IOException exception) {
                JafaarClient.LOGGER.error("Could not load client config", exception);
            }
        }
    }

    public static boolean get(String key, boolean fallback) {
        return Boolean.parseBoolean(VALUES.getProperty(key, Boolean.toString(fallback)));
    }

    public static void set(String key, boolean value) {
        VALUES.setProperty(key, Boolean.toString(value));
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream output = Files.newOutputStream(FILE)) {
                VALUES.store(output, "JAFAAR HACK CLIENT settings");
            }
        } catch (IOException exception) {
            JafaarClient.LOGGER.error("Could not save client config", exception);
        }
    }

    private static final class FabricConfigPath {
        private static Path path() {
            return net.fabricmc.loader.api.FabricLoader.getInstance()
                    .getConfigDir().resolve("jafaar-hack-client.properties");
        }
    }
}
