package town.bunger.towns.impl.town;

import com.google.gson.JsonObject;
import net.kyori.adventure.audience.Audience;
import org.apiguardian.api.API;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.event.resident.JoinTownResidentEvent;
import town.bunger.towns.api.event.resident.LeaveTownResidentEvent;
import town.bunger.towns.api.event.town.DeleteTownEvent;
import town.bunger.towns.api.event.town.KickResidentTownEvent;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.impl.resident.ResidentImpl;
import town.bunger.towns.impl.resident.WrappedResidentView;
import town.bunger.towns.plugin.db.Tables;
import town.bunger.towns.plugin.db.tables.records.TownRecord;
import town.bunger.towns.plugin.jooq.JsonUtil;
import town.bunger.towns.plugin.util.MainThreadExecutor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

public final class TownImpl implements Town {

    private final BungerTownsImpl api;
    private final int id;
    private volatile String name;
    private final LocalDateTime created;
    private final UUID ownerId;
    private volatile boolean open;
    private final boolean public_;
    private volatile @Nullable String slogan;
    private final JsonObject metadata;
    private final ConcurrentSkipListSet<UUID> residents;

    /**
     * Constructor for creating a new town.
     *
     * @param id      The ID of the town
     * @param builder The creation data
     */
    public TownImpl(BungerTownsImpl api, int id, BuilderImpl builder) {
        this.api = api;
        this.id = id;
        this.name = Objects.requireNonNull(builder.name, "name");
        this.created = builder.created;
        this.ownerId = Objects.requireNonNull(builder.owner, "owner").id();
        this.open = builder.open;
        this.public_ = builder.public_;
        this.slogan = builder.slogan;
        this.metadata = builder.metadata;
        this.residents = new ConcurrentSkipListSet<>();
        this.residents.add(this.ownerId);
    }

    /**
     * Constructor for loading from the database.
     * <strong>PRECONDITION: The owner {@link Resident} must be already loaded.</strong>
     *
     * @param api    The API instance
     * @param record The database record
     */
    public TownImpl(BungerTownsImpl api, TownRecord record, Collection<UUID> residents) {
        this.api = api;
        this.id = record.getId();
        this.name = record.getName();
        this.created = record.getCreated();
        this.ownerId = record.getOwnerId();
        this.open = record.getOpen();
        this.public_ = record.getPublic();
        this.slogan = record.getSlogan();
        this.metadata = JsonUtil.fromJooq(record.getMetadata());
        this.residents = new ConcurrentSkipListSet<>(residents);
    }

    /**
     * Gets the database ID of the town.
     *
     * @return The database ID
     */
    @API(status = API.Status.INTERNAL)
    public int id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public CompletableFuture<@Nullable Void> setName(String name) {
        Objects.requireNonNull(name, "Cannot set town name to null");
        if (this.api.towns().contains(name)) {
            throw new IllegalArgumentException("Town with name " + name + " already exists");
        }

        final String oldName = this.name;
        return this.api.db().ctx()
            .update(Tables.TOWN)
            .set(Tables.TOWN.NAME, name)
            .where(Tables.TOWN.ID.eq(this.id))
            .executeAsync()
            .thenAccept(rows -> {
                if (rows != 1) {
                    throw new IllegalStateException("Failed to update town name");
                }
                this.api.towns().updateName(this.id, oldName, name);
                this.name = name;
            })
            .toCompletableFuture();
    }

    @Override
    public LocalDateTime created() {
        return this.created;
    }

    @Override
    public @Nullable ResidentImpl owner() {
        return this.api.residents().get(this.ownerId);
    }

    @Override
    public UUID ownerId() {
        return this.ownerId;
    }

    @Override
    public String ownerName() {
        return Objects.requireNonNull(this.api.residents().getName(this.ownerId), "Town owner somehow doesn't exist");
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public CompletableFuture<@Nullable Void> setOpen(boolean open) {
        if (this.open == open) {
            // No change
            return CompletableFuture.completedFuture(null);
        }

        return this.api.db().ctx()
            .update(Tables.TOWN)
            .set(Tables.TOWN.OPEN, open)
            .where(Tables.TOWN.ID.eq(this.id))
            .executeAsync()
            .toCompletableFuture()
            .thenAccept($ -> this.open = open);
    }

    @Override
    public boolean isPublic() {
        return this.public_;
    }

    @Override
    public @Nullable String slogan() {
        return this.slogan;
    }

    @Override
    public CompletableFuture<@Nullable Void> setSlogan(@Nullable String slogan) {
        return this.api.db().ctx()
            .update(Tables.TOWN)
            .set(Tables.TOWN.SLOGAN, slogan)
            .where(Tables.TOWN.ID.eq(this.id))
            .executeAsync()
            .thenAccept(rows -> {
                if (rows != 1) {
                    throw new IllegalStateException("Failed to update town slogan");
                }
                this.slogan = slogan;
            })
            .toCompletableFuture();
    }

    @Override
    public JsonObject metadata() {
        return this.metadata;
    }

    @Override
    public Collection<UUID> residentIds() {
        return List.copyOf(this.residents);
    }

    @Override
    public Collection<String> residentNames() {
        return this.api.residents().getAllNames(this.residents).values();
    }

    @Override
    public Collection<ResidentImpl> loadedResidents() {
        return this.api.residents().getAll(this.residents).values();
    }

    @Override
    public CompletableFuture<? extends Collection<ResidentImpl>> residents() {
        return this.api.residents().loadOrCreatePlayers(this.residents).thenApply(residents -> List.copyOf(residents.values()));
    }

    @Override
    public CompletableFuture<Boolean> kick(Resident r) {
        if (!(r instanceof ResidentImpl resident)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid resident type"));
        }

        final Integer id = resident.townId();
        if (id == null || id != this.id) {
            // Not in this town
            return CompletableFuture.completedFuture(false);
        }

        // Let plugins cancel kicking a resident
        var kickEvent = new KickResidentTownEvent(new WrappedTownView(this), new WrappedResidentView(resident));
        CompletableFuture<Boolean> cancelledFuture = MainThreadExecutor.callEventAsync(kickEvent);

        return cancelledFuture.thenCompose(cancelled -> {
            if (cancelled) {
                return CompletableFuture.completedFuture(false);
            }
            return resident.setTown(null)
                .thenApply($ -> this.residents.remove(resident.id()))
                .exceptionally($ -> false);
        });
    }

    /**
     * Use {@link Resident#joinTown(Town)} instead.
     *
     * @param resident The resident to add
     * @return True if the resident was added or was already in the town,
     * false if they were in a different town or the addition was cancelled
     */
    @API(status = API.Status.INTERNAL)
    public CompletableFuture<Boolean> addResident(ResidentImpl resident) {
        final Integer id = resident.townId();
        if (id != null) {
            // Already in a town
            // true  = same town
            // false = different town
            return CompletableFuture.completedFuture(id == this.id);
        }

        // Let plugins cancel joining a town
        var joinEvent = new JoinTownResidentEvent(new WrappedResidentView(resident), new WrappedTownView(this));
        CompletableFuture<Boolean> cancelledFuture = MainThreadExecutor.callEventAsync(joinEvent);

        return cancelledFuture.thenCompose(cancelled -> {
            if (cancelled) {
                return CompletableFuture.completedFuture(false);
            }
            return resident.setTown(this)
                .thenApply($ -> this.residents.add(resident.id()))
                .exceptionally($ -> false);
        });
    }

    /**
     * Use {@link Resident#leaveTown()} instead.
     *
     * @param resident The resident to remove
     * @return True if the resident was removed, false if they weren't in this town or the removal was cancelled
     */
    @API(status = API.Status.INTERNAL)
    public CompletableFuture<Boolean> removeResident(ResidentImpl resident) {
        final Integer id = resident.townId();
        if (id == null || id != this.id) {
            // Not in this town
            return CompletableFuture.completedFuture(false);
        }

        // Let plugins cancel leaving a town
        var leaveEvent = new LeaveTownResidentEvent(new WrappedResidentView(resident), new WrappedTownView(this));
        CompletableFuture<Boolean> cancelledFuture = MainThreadExecutor.callEventAsync(leaveEvent);

        return cancelledFuture.thenCompose(cancelled -> {
            if (cancelled) {
                return CompletableFuture.completedFuture(false);
            }
            return resident.setTown(null)
                .thenApply($ -> this.residents.remove(resident.id()))
                .exceptionally($ -> false);
        });
    }

    @Override
    public boolean hasResident(UUID uuid) {
        return this.residents.contains(uuid);
    }

    @Override
    public CompletableFuture<Boolean> delete() {
        // Let plugins cancel deleting a town
        var deleteEvent = new DeleteTownEvent(new WrappedTownView(this));
        CompletableFuture<Boolean> cancelledFuture = MainThreadExecutor.callEventAsync(deleteEvent);

        return cancelledFuture.thenCompose(cancelled -> {
            if (cancelled) {
                return CompletableFuture.completedFuture(false);
            }
            // Remove all residents from the town
            var removeResidentsFuture = CompletableFuture.allOf(
                this.loadedResidents().stream()
                    .map(resident -> resident.setTown(null))
                    .toArray(CompletableFuture[]::new)
            );
            // Then delete the town
            return removeResidentsFuture.thenCompose($ -> this.api.towns().delete(this));
        });
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return List.copyOf(this.loadedResidents());
    }

    public static final class BuilderImpl implements Town.Builder {

        public @Nullable String name;
        public LocalDateTime created = LocalDateTime.now();
        public @Nullable ResidentImpl owner;
        public boolean open;
        public boolean public_;
        public @Nullable String slogan;
        public JsonObject metadata = new JsonObject();

        @Override
        public Builder name(String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        @Override
        public Builder created(LocalDateTime created) {
            this.created = Objects.requireNonNull(created, "created");
            return this;
        }

        @Override
        public Builder owner(Resident owner) {
            Objects.requireNonNull(owner, "owner");
            if (!(owner instanceof ResidentImpl)) {
                throw new IllegalArgumentException("Invalid owner Resident type");
            }
            this.owner = (ResidentImpl) owner;
            return this;
        }

        @Override
        public Builder open(boolean open) {
            this.open = open;
            return this;
        }

        @Override
        public Builder public_(boolean public_) {
            this.public_ = public_;
            return this;
        }

        @Override
        public Builder slogan(String slogan) {
            this.slogan = slogan;
            return this;
        }

        @Override
        public Builder metadata(JsonObject metadata) {
            this.metadata = Objects.requireNonNull(metadata, "metadata");
            return this;
        }

        @API(status = API.Status.INTERNAL)
        public TownRecord toRecord() {
            var record = new TownRecord();
            record.setName(Objects.requireNonNull(this.name, "name"));
            record.setOwnerId(Objects.requireNonNull(this.owner, "owner").id());
            record.setOpen(this.open);
            record.setPublic(this.public_);
            record.setMetadata(JsonUtil.toJooq(Objects.requireNonNull(this.metadata, "metadata")));
            return record;
        }
    }
}
