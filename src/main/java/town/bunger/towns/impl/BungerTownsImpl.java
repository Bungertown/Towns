package town.bunger.towns.impl;

import org.slf4j.Logger;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.impl.resident.ResidentManagerImpl;
import town.bunger.towns.impl.town.TownManagerImpl;
import town.bunger.towns.plugin.config.ConfigManager;
import town.bunger.towns.plugin.db.DatabaseManager;

public final class BungerTownsImpl implements BungerTowns {

    private final Logger logger;
    private final ConfigManager config;
    private final DatabaseManager db;
    private final TownManagerImpl towns;
    private final ResidentManagerImpl residents;

    public BungerTownsImpl(Logger logger, ConfigManager config, DatabaseManager db) {
        this.logger = logger;
        this.config = config;
        this.db = db;
        this.towns = new TownManagerImpl(this);
        this.residents = new ResidentManagerImpl(this);
    }

    public void loadAllIds() {
        this.residents.loadAllIds();
        this.towns.loadAllIds();
    }

    public Logger logger() {
        return this.logger;
    }

    public ConfigManager config() {
        return this.config;
    }

    public DatabaseManager db() {
        return this.db;
    }

    @Override
    public TownManagerImpl towns() {
        return this.towns;
    }

    @Override
    public ResidentManagerImpl residents() {
        return this.residents;
    }
}
