package town.bunger.towns.api.town;

import com.google.gson.JsonObject;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.resident.ResidentView;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TownView extends ForwardingAudience {

    /**
     * Gets the name of the town.
     *
     * @return The town's name
     */
    String name();

    /**
     * Gets the time the town was created.
     *
     * @return The creation time
     */
    LocalDateTime created();

    /**
     * Gets the owner of the town.
     *
     * @return The town's owner, or null if the owner is not loaded
     */
    @Nullable ResidentView owner();

    /**
     * Gets the UUID of the owner of the town.
     *
     * @return The town's owner's UUID
     */
    UUID ownerId();

    /**
     * Gets the name of the owner of the town.
     *
     * @return The town's owner's name
     */
    String ownerName();

    /**
     * Whether the town will accepts new residents without an invitation.
     *
     * @return True if the town accepts new residents without an invitation
     */
    boolean isOpen();

    /**
     * Whether the town can be teleported to by anyone.
     *
     * @return True if the town can be teleported to by anyone
     */
    boolean isPublic();

    /**
     * Gets the slogan of the town.
     *
     * @return The town's slogan, or null if the slogan has not been set
     */
    @Nullable String slogan();

    /**
     * Returns extra data stored by addons for the town.
     *
     * @return The extra data
     */
    JsonObject metadata();

    /**
     * Gets a collection of all UUIDs of residents in the town.
     *
     * @return All resident UUIDs
     */
    Collection<UUID> residentIds();

    /**
     * Gets a collection of all loaded residents in the town.
     *
     * @return All loaded residents
     */
    Collection<? extends ResidentView> loadedResidents();

    /**
     * Gets a collection of all residents in the town.
     *
     * @return An asynchronous future returning a collection of all residents in the town
     */
    CompletableFuture<? extends Collection<? extends ResidentView>> residents();

    /**
     * Whether the town has a {@link Resident} with the given UUID.
     *
     * @param uuid The UUID of the resident
     * @return True if the town has a resident with the given UUID
     */
    boolean hasResident(UUID uuid);
}
