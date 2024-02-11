package town.bunger.towns.api.resident;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A manager for {@link Resident}s.
 */
public interface ResidentManager {

    /**
     * Gets a collection of all resident UUIDs, loaded or not.
     *
     * @return All resident UUIDs
     */
    Collection<UUID> all();

    /**
     * Gets a collection of all {@link Resident} names, loaded or not.
     *
     * @return All resident names
     */
    Collection<String> allNames();

    /**
     * Gets a collection of all loaded {@link Resident}s.
     *
     * @return All loaded residents
     */
    Collection<? extends Resident> loaded();

    /**
     * Gets a loaded resident by UUID.
     *
     * @param uuid The UUID of the resident
     * @return The resident, or null if it doesn't exist or isn't loaded
     */
    @Nullable Resident get(UUID uuid);

    /**
     * Gets a loaded {@link Resident} by name.
     *
     * @param name The name of the resident
     * @return The resident, or null if it doesn't exist or isn't loaded
     */
    @Nullable Resident get(String name);

    /**
     * Gets a resident's name by UUID.
     *
     * <p>Even if the resident is not loaded, this will still return their name.</p>
     *
     * @param uuid The UUID of the resident
     * @return The resident's name, or null if it doesn't exist
     */
    @Nullable String getName(UUID uuid);

    /**
     * Gets a resident's UUID by name.
     *
     * @param name The name of the resident
     * @return The resident's UUID, or null if it doesn't exist
     */
    @Nullable UUID getUUID(String name);

    /**
     * Gets a collection of loaded residents by UUIDs.
     *
     * @param uuids The UUIDs of the residents
     * @return An unmodifiable map of UUIDs to residents
     */
    Map<UUID, ? extends Resident> getAll(Collection<UUID> uuids);

    /**
     * Gets a collection of resident names by UUIDs.
     *
     * @param uuids The UUIDs of the residents
     * @return An unmodifiable map of UUIDs to resident names
     */
    Map<UUID, String> getAllNames(Collection<UUID> uuids);

    /**
     * Loads a resident by UUID. If the resident does not exist, it will be created.
     *
     * @param uuid The UUID of the resident
     * @return An asynchronous future returning the resident, or null if it doesn't exist
     */
    CompletableFuture<? extends Resident> loadOrCreatePlayer(UUID uuid);

    /**
     * Attempts to load a player {@link Resident} by name.
     *
     * @param name The name of the resident
     * @return An asynchronous future returning the resident, or null if it doesn't exist
     */
    CompletableFuture<? extends @Nullable Resident> loadPlayer(String name);

    /**
     * Loads a collection of residents by UUID.
     *
     * @param uuids The UUIDs of the residents
     * @return An asynchronous future returning a map of UUIDs to residents
     */
    CompletableFuture<? extends Map<UUID, ? extends Resident>> loadOrCreatePlayers(Collection<UUID> uuids);
}
