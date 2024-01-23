package town.bunger.towns.impl.resident;

import com.google.gson.JsonObject;
import net.kyori.adventure.audience.Audience;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.impl.town.TownImpl;
import town.bunger.towns.plugin.db.Tables;
import town.bunger.towns.plugin.db.tables.records.ResidentRecord;
import town.bunger.towns.plugin.jooq.JsonUtil;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ResidentImpl implements Resident {

    private final BungerTownsImpl api;
    private final UUID uuid;
    private final String name;
    private final LocalDateTime created;
    private final @Nullable LocalDateTime lastJoined;
    private @Nullable Integer townId;
    private final JsonObject metadata;

    /**
     * Whether the resident is online. Not applicable to NPCs.
     *
     * <p>This is volatile to allow the resident's cache expiry to read it from
     * any thread, and to write it from an event listener in the main thread.</p>
     */
    @API(status = API.Status.INTERNAL)
    public volatile boolean online = false;

    /**
     * Constructor for loading from the database.
     *
     * @param api The API instance, for mutating the resident
     * @param record The database record
     */
    public ResidentImpl(BungerTownsImpl api, ResidentRecord record) {
        this.api = api;
        this.uuid = record.getId();
        this.name = record.getName();
        this.created = record.getCreated();
        this.lastJoined = record.getLastJoined();
        this.townId = record.getTownId();
        this.metadata = JsonUtil.fromJooq(record.getMetadata());
    }

    @Override
    public UUID id() {
        return this.uuid;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public LocalDateTime created() {
        return this.created;
    }

    @Override
    public @Nullable LocalDateTime lastJoined() {
        return this.lastJoined;
    }

    @Override
    public boolean hasTown() {
        return this.townId != null;
    }

    @Override
    public @Nullable TownImpl town() {
        if (this.townId == null) {
            return null;
        }
        return this.api.towns().get(this.townId);
    }

    @Override
    public CompletableFuture<@Nullable TownImpl> loadTown() {
        if (this.townId == null) {
            return CompletableFuture.completedFuture(null);
        }
        return this.api.towns().load(this.townId);
    }

    /**
     * Gets the database ID of the town.
     *
     * @return The database ID
     */
    @API(status = API.Status.INTERNAL)
    public @Nullable Integer townId() {
        return this.townId;
    }

    @Override
    public @Nullable String townName() {
        if (this.townId == null) {
            return null;
        }
        return this.api.towns().getName(this.townId);
    }

    /**
     * Use {@link #joinTown(Town)} and {@link #leaveTown()} instead, which perform additional bookkeeping.
     *
     * @param town The new town, or null
     */
    @API(status = API.Status.INTERNAL)
    public void setTown(@Nullable TownImpl town) {
        if (this.townId != null && town != null) {
            throw new IllegalStateException(
                "Resident tried to swap towns, " +
                "use Resident#leaveTown() followed by Resident#joinTown(Town) instead"
            );
        }

        this.townId = town == null ? null : town.id();

        final Integer id = town == null ? null : town.id();
        // TODO: switch to synchronous, or add atomic update?
        this.api.db().ctx()
            .update(Tables.RESIDENT)
            .set(Tables.RESIDENT.TOWN_ID, id)
            .where(Tables.RESIDENT.ID.eq(this.id()))
            .executeAsync();
    }

    @Override
    public boolean leaveTown() {
        if (this.townId == null) {
            // Not in a town
            return true;
        }

        final TownImpl town = this.api.towns().get(this.townId);
        if (town != null) {
            return town.removeResident(this);
        }
        return true;
    }

    @Override
    public boolean joinTown(Town town) {
        Objects.requireNonNull(town, "Town was null, did you mean to call Resident#leaveTown()?");
        if (!(town instanceof TownImpl)) {
            throw new IllegalArgumentException("Invalid town type");
        }

        return ((TownImpl) town).addResident(this);
    }

    @Override
    public JsonObject metadata() {
        return this.metadata;
    }

    @Override
    public @NotNull Audience audience() {
        final Player player = Bukkit.getPlayer(this.uuid);
        return Objects.requireNonNullElseGet(player, Audience::empty);
    }
}
