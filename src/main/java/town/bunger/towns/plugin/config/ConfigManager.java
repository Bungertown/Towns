package town.bunger.towns.plugin.config;

import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import town.bunger.towns.plugin.BungerTownsPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ConfigManager {

    private final DatabaseConfig database;

    public ConfigManager(BungerTownsPlugin plugin) throws IOException {
        this.database = loadDatabase(plugin);
    }

    public DatabaseConfig database() {
        return this.database;
    }

    private static DatabaseConfig loadDatabase(BungerTownsPlugin plugin) throws IOException {
        final Path configPath = plugin.getDataFolder().toPath().resolve("database.conf");
        if (!Files.exists(configPath)) {
            try (var defaultConfig = ConfigManager.class.getResourceAsStream("database.conf")) {
                Files.copy(Objects.requireNonNull(defaultConfig), configPath);
            }
        }

        return HoconConfigurationLoader.builder()
            .path(configPath)
            .build()
            .load()
            .get(DatabaseConfig.class);
    }

}
