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

    @Override
    @Nullable Resident owner();

    @Override
    CompletableFuture<? extends Collection<? extends Resident>> residents();

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
         * Sets extra data stored by addons for the town.
         *
         * @param metadata The new metadata
         * @return This builder
         */
        Builder metadata(JsonObject metadata);
    }
}
