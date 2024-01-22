package town.bunger.towns.api;

import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.resident.ResidentManager;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.api.town.TownManager;

/**
 * The BungerTowns API.
 */
public interface BungerTowns {

    /**
     * Gets the {@link Town} manager.
     *
     * @return The town manager
     */
    TownManager towns();

    /**
     * Gets the {@link Resident} manager.
     *
     * @return The resident manager
     */
    ResidentManager residents();

}
