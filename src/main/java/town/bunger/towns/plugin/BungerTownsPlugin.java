package town.bunger.towns.plugin;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.BungerTownsProvider;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.plugin.command.CommandManager;
import town.bunger.towns.plugin.config.ConfigManager;
import town.bunger.towns.plugin.db.DatabaseManager;
import town.bunger.towns.plugin.listener.ResidentListener;

import java.io.IOException;
import java.nio.file.Files;

public final class BungerTownsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!Files.exists(this.getDataFolder().toPath())) {
            try {
                Files.createDirectories(this.getDataFolder().toPath());
            } catch (IOException e) {
                this.getSLF4JLogger().error("Failed to create data folder!", e);
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        ConfigManager config;
        try {
            config = new ConfigManager(this);
        } catch (IOException e) {
            this.getSLF4JLogger().error("Failed to load configs", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        var database = new DatabaseManager(config.database(), this);
        database.migrate(config.database(), this);

        var api = new BungerTownsImpl(this.getSLF4JLogger(), config, database);
        api.loadAllIds();
        this.getServer().getServicesManager().register(BungerTowns.class, api, this, ServicePriority.High);
        BungerTownsProvider.register(api);

        var commands = new CommandManager(this);
        commands.register(this, api);

        this.getServer().getPluginManager().registerEvents(new ResidentListener(api), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
