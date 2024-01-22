package town.bunger.towns.api.resident;

import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.town.Town;

import java.util.concurrent.CompletableFuture;

public interface Resident extends ResidentView {

    @Override
    @Nullable Town town();

    @Override
    CompletableFuture<? extends @Nullable Town> loadTown();

    /**
     * Leaves the town the resident is a member of.
     *
     * @return True if the resident's town was left or if the resident was not a member of a town
     */
    boolean leaveTown();

    /**
     * Joins a town.
     *
     * @param town The town
     * @return True if the new town was joined or if the resident was already a member of the town
     */
    boolean joinTown(Town town);
}
