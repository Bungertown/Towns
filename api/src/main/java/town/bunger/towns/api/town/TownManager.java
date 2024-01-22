package town.bunger.towns.api.town;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * A manager for {@link Town}s.
 */
public interface TownManager {

    /**
     * Gets a collection of all town names, loaded or not.
     *
     * @return All town names
     */
    Collection<String> all();

    /**
     * Gets a collection of all loaded towns.
     *
     * @return All loaded towns
     */
    Collection<? extends Town> loaded();

    /**
     * Gets a town by name.
     *
     * @param name The name of the town
     * @return The town, or null if it doesn't exist or isn't loaded
     */
    @Nullable Town get(String name);

    /**
     * Loads a town by name.
     *
     * @param name The name of the town
     * @return An asynchronous future returning the town, or null if it doesn't exist
     */
    CompletableFuture<? extends @Nullable Town> load(String name);

    /**
     * Creates a town from a builder.
     *
     * @param builder The town creation data
     * @return An asynchronous future returning the new town
     */
    CompletableFuture<? extends Town> create(Town.Builder builder);

    /**
     * Creates a new town builder.
     *
     * @return The new builder
     */
    Town.Builder builder();
}
