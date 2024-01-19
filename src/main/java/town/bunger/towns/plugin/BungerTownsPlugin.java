package town.bunger.towns.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import town.bunger.towns.plugin.config.ConfigManager;
import town.bunger.towns.plugin.db.DatabaseManager;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public final class BungerTownsPlugin extends JavaPlugin {

    private static BungerTownsPlugin instance = null;

    public static BungerTownsPlugin getInstance() {
        return Objects.requireNonNull(instance, "instance");
    }

    private @Nullable ConfigManager config = null;
    private @Nullable DatabaseManager database = null;

    @Override
    public void onEnable() {
        instance = this;

        if (!Files.exists(this.getDataFolder().toPath())) {
            try {
                Files.createDirectories(this.getDataFolder().toPath());
            } catch (IOException e) {
                this.getSLF4JLogger().error("Failed to create data folder!", e);
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        try {
            this.config = new ConfigManager(this);
        } catch (IOException e) {
            this.getSLF4JLogger().error("Failed to load configs", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.database = new DatabaseManager(this.config.database(), this);
        this.database.migrate(this.config.database(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
