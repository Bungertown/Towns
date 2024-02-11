package town.bunger.towns.api.town;

import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.Resident;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * A group of residents and claimed chunks under a single name.
 */
public interface Town extends TownView {

    /**
     * Sets the name of the town.
     *
     * @param name The new name
     */
    CompletableFuture<@Nullable Void> setName(String name);

    @Override
    @Nullable Resident owner();

    /**
     * Sets the open status of the town.
     *
     * @param open The new open status
     */
    CompletableFuture<@Nullable Void> setOpen(boolean open);

    /**
     * Sets the slogan of the town.
     *
     * @param slogan The new slogan
     */
    CompletableFuture<@Nullable Void> setSlogan(@Nullable String slogan);

    @Override
    CompletableFuture<? extends Collection<? extends Resident>> residents();

    /**
     * Kicks a resident from the town.
     *
     * @param resident The resident to kick
     * @return True if the resident was kicked
     */
    CompletableFuture<Boolean> kick(Resident resident);

    /**
     * Asynchronously deletes the town.
     *
     * @return A future that completes with {@code true} when the town is deleted,
     * or {@code false} if it could not be deleted
     */
    CompletableFuture<Boolean> delete();

    interface Builder {

        /**
         * Sets the name of the town.
         *
         * @param name The new name
         * @return This builder
         */
        Builder name(String name);

        /**
         * Sets the time the town was created.
         *
         * @param created The new creation time
         * @return This builder
         */
        Builder created(LocalDateTime created);

        /**
         * Sets the owner of the town.
         *
         * @param owner The new owner
         * @return This builder
         */
        Builder owner(Resident owner);

        /**
         * Sets whether the town will accepts new residents without an invitation.
         *
         * @param open True if the new town accepts new residents without an invitation
         * @return This builder
         */
        Builder open(boolean open);

        /**
         * Sets whether the town can be teleported to by anyone.
         *
         * @param public_ True if the new town can be teleported to by anyone
         * @return This builder
         */
        Builder public_(boolean public_);

        /**
         * Sets the slogan of the town.
         *
         * @param slogan The new slogan
         * @return This builder
         */
        Builder slogan(String slogan);

        /**
         * Sets extra data stored by addons for the town.
         *
         * @param metadata The new metadata
         * @return This builder
         */
        Builder metadata(JsonObject metadata);
    }
}
