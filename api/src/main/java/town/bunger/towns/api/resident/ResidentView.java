package town.bunger.towns.api.resident;

import com.google.gson.JsonObject;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.town.TownView;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ResidentView extends ForwardingAudience.Single {

    /**
     * Gets the UUID of the resident.
     *
     * @return The UUID
     */
    UUID id();

    /**
     * Gets the name of the resident.
     *
     * @return The name
     */
    String name();

    /**
     * Gets the time the resident was created.
     *
     * @return The creation time
     */
    Instant created();

    /**
     * Gets the last time the resident joined the server.
     *
     * @return The last join time
     */
    @Nullable Instant lastJoined();

    /**
     * Whether the resident is a member of a town.
     *
     * @return True if the resident is a member of a town, even if the town is not loaded
     */
    boolean hasTown();

    /**
     * Gets the town the resident is a member of.
     *
     * @return The town, or null if the resident is not a member of a town or the town is not loaded
     */
    @Nullable TownView town();

    /**
     * Loads the town the resident is a member of.
     *
     * @return The town, or null if the resident is not a member of a town
     */
    CompletableFuture<? extends @Nullable TownView> loadTown();

    /**
     * Gets the name of the town the resident is a member of.
     *
     * <p>This will provide the name of the town even if the town is not loaded.</p>
     *
     * @return The town name, or null if the resident is not a member of a town
     */
    @Nullable String townName();

    /**
     * Returns extra data stored by addons for the resident.
     *
     * @return The extra data
     */
    JsonObject metadata();
}
