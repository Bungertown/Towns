package town.bunger.towns.plugin.config;

import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import town.bunger.towns.plugin.BungerTownsPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ConfigManager {

    private final MainConfig config;
    private final DatabaseConfig database;

    public ConfigManager(BungerTownsPlugin plugin) throws IOException {
        this.config = loadConfig(plugin);
        this.database = loadDatabase(plugin);
    }

    public MainConfig get() {
        return this.config;
    }

    public DatabaseConfig database() {
        return this.database;
    }

    private static MainConfig loadConfig(BungerTownsPlugin plugin) throws IOException {
        final Path configPath = plugin.getDataFolder().toPath().resolve("config.conf");
        if (!Files.exists(configPath)) {
            try (var defaultConfig = ConfigManager.class.getResourceAsStream("config.conf")) {
                Files.copy(Objects.requireNonNull(defaultConfig), configPath);
            }
        }

        final var loader = HoconConfigurationLoader.builder()
            .path(configPath)
            .build()
            .load();

        return Objects.requireNonNull(loader.get(MainConfig.class), "main config");
    }

    private static DatabaseConfig loadDatabase(BungerTownsPlugin plugin) throws IOException {
        final Path configPath = plugin.getDataFolder().toPath().resolve("database.conf");
        if (!Files.exists(configPath)) {
            try (var defaultConfig = ConfigManager.class.getResourceAsStream("database.conf")) {
                Files.copy(Objects.requireNonNull(defaultConfig), configPath);
            }
        }

        final var loader = HoconConfigurationLoader.builder()
            .path(configPath)
            .build()
            .load();

        return Objects.requireNonNull(loader.get(DatabaseConfig.class), "database config");
    }

}
