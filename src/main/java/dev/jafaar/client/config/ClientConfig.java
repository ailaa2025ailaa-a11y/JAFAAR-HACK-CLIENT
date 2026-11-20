package dev.jafaar.client.config;

import dev.jafaar.client.module.ClientModule;
import dev.jafaar.client.module.ModuleManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ClientConfig {
    private final Path path;

    public ClientConfig(Path path) {
        this.path = path;
    }

    public void load(ModuleManager manager) {
        if (!Files.exists(path)) return;
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
            for (ClientModule module : manager.getModules()) {
                String value = properties.getProperty("module." + module.id());
                if (value != null) module.setEnabled(Boolean.parseBoolean(value));
            }
        } catch (IOException exception) {
            System.err.println("Could not load JAFAAR CLIENT config: " + exception.getMessage());
        }
    }

    public void save(ModuleManager manager) {
        Properties properties = new Properties();
        for (ClientModule module : manager.getModules()) {
            properties.setProperty("module." + module.id(), Boolean.toString(module.isEnabled()));
        }
        try {
            Files.createDirectories(path.getParent());
            try (OutputStream output = Files.newOutputStream(path)) {
                properties.store(output, "JAFAAR CLIENT configuration");
            }
        } catch (IOException exception) {
            System.err.println("Could not save JAFAAR CLIENT config: " + exception.getMessage());
        }
    }
}
