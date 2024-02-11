package town.bunger.towns.impl.town;

import com.github.benmanes.caffeine.cache.*;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.event.town.CreateTownEvent;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.api.town.TownManager;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.impl.resident.ResidentImpl;
import town.bunger.towns.impl.resident.WrappedResidentView;
import town.bunger.towns.plugin.db.Tables;
import town.bunger.towns.plugin.db.tables.records.TownRecord;
import town.bunger.towns.plugin.util.MainThreadExecutor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static org.jooq.impl.DSL.multiset;

public final class TownManagerImpl implements TownManager {

    private final BungerTownsImpl api;
    private final AsyncLoadingCache<Integer, TownImpl> cache;
    private final Cache<String, Integer> namesToIds;

    public TownManagerImpl(BungerTownsImpl api) {
        this.api = api;

        this.cache = Caffeine.newBuilder()
            .scheduler(Scheduler.systemScheduler())
            .expireAfter(new TownExpiry(this.api))
            .buildAsync(new TownLoader(this.api));
        this.namesToIds = Caffeine.newBuilder()
            .<String, Integer>removalListener((key, value, cause) -> {
                if (value != null) {
                    // If the town is deleted, invalidate the cache
                    this.cache.synchronous().invalidate(value);
                }
            })
            .build();
    }

    /**
     * Loads all town IDs from the database for performant existence checks and name lookup.
     */
    @API(status = API.Status.INTERNAL)
    public void loadAllIds() {
        this.api.logger().info("Loading all town IDs...");
        this.api.db().ctx()
            .select(Tables.TOWN.ID, Tables.TOWN.NAME)
            .from(Tables.TOWN)
            .fetchAsync()
            .thenAccept(result -> {
                for (var record : result) {
                    this.namesToIds.put(record.value2(), record.value1());
                }
                this.api.logger().info("Loaded {} town IDs", result.size());
            });
    }

    @Override
    public Collection<String> allNames() {
        return List.copyOf(this.namesToIds.asMap().keySet());
    }

    @Override
    public Collection<TownImpl> loaded() {
        return List.copyOf(this.cache.synchronous().asMap().values());
    }

    @Override
    public CompletableFuture<? extends Collection<TownImpl>> all() {
        return this.cache
            .getAll(this.namesToIds.asMap().values())
            .thenApply(towns -> List.copyOf(towns.values()));
    }

    /**
     * Gets the town with the given database ID.
     *
     * @param id The town ID
     * @return The town, or null if it doesn't exist or isn't loaded
     */
    @API(status = API.Status.INTERNAL)
    public @Nullable TownImpl get(int id) {
        return this.cache.synchronous().getIfPresent(id);
    }

    @Override
    public @Nullable TownImpl get(String name) {
        final Integer id = this.namesToIds.getIfPresent(name);
        if (id == null) {
            // Town not found
            return null;
        }
        return this.cache.synchronous().get(id);
    }

    /**
     * Gets the name of the town with the given ID.
     *
     * <p>Even if the town is not loaded, this method will still return the name.</p>
     *
     * @param id The town ID
     * @return The town name, or null if it doesn't exist
     */
    @API(status = API.Status.INTERNAL)
    public @Nullable String getName(int id) {
        // TODO: speed up with a reverse cache?
        return this.namesToIds.asMap().entrySet().stream()
            .filter(entry -> entry.getValue().equals(id))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }

    /**
     * Loads the town with the given database ID.
     *
     * @param id The town ID
     * @return An asynchronous future returning the town, or null if it doesn't exist
     */
    @API(status = API.Status.INTERNAL)
    public CompletableFuture<@Nullable TownImpl> load(int id) {
        return this.cache.get(id);
    }

    @Override
    public CompletableFuture<@Nullable TownImpl> load(String name) {
        final Integer id = this.namesToIds.getIfPresent(name);
        if (id == null) {
            // Town not found
            return CompletableFuture.completedFuture(null);
        }
        return this.cache.get(id);
    }

    @Override
    public boolean contains(String name) {
        return this.namesToIds.getIfPresent(name) != null;
    }

    /**
     * Updates the name cache when a town's name is changed.
     *
     * @param id The town's database ID
     * @param oldName The old name
     * @param newName The new name
     */
    @API(status = API.Status.INTERNAL)
    public void updateName(int id, String oldName, String newName) {
        this.namesToIds.invalidate(oldName);
        this.namesToIds.put(newName, id);
    }

    @Override
    public CompletableFuture<TownImpl> create(Town.Builder builder) {
        if (!(builder instanceof TownImpl.BuilderImpl data)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid builder type"));
        }

        // Make sure a town with the same name doesn't already exist
        final String name = Objects.requireNonNull(data.name, "name");
        if (this.namesToIds.getIfPresent(name) != null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Town with name '" + name + "' already exists"));
        }

        // Make sure the owner isn't already in a town
        final ResidentImpl owner = Objects.requireNonNull(data.owner, "owner");
        if (owner.hasTown()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Owner is already in a town"));
        }

        return CompletableFuture
            .runAsync(() -> {
                // Let other plugins cancel town creation
                var createEvent = new CreateTownEvent(new TownBuilderView(data), new WrappedResidentView(owner));
                Bukkit.getPluginManager().callEvent(createEvent);
                if (createEvent.isCancelled()) {
                    throw new IllegalStateException("Town creation was cancelled");
                }
            }, MainThreadExecutor.INSTANCE)
            .thenCompose($ -> this.api.db().ctx()
                .insertInto(Tables.TOWN)
                .set(data.toRecord())
                .returningResult(Tables.TOWN.ID)
                .fetchAsync())
            .thenApply(result -> {
                if (result.isEmpty()) {
                    throw new IllegalStateException("Failed to insert town " + name);
                }

                final TownImpl town = new TownImpl(this.api, result.get(0).getValue(Tables.TOWN.ID), data);

                // Update the name cache
                this.namesToIds.put(name, town.id());
                // Insert the town into the cache
                this.cache.synchronous().put(town.id(), town);
                // Update the owner's town
                owner.setTown(town);

                return town;
            })
            .toCompletableFuture();
    }

    @Override
    public TownImpl.BuilderImpl builder() {
        return new TownImpl.BuilderImpl();
    }

    /**
     * Asynchronously deletes a town.
     *
     * @return A future that completes with {@code true} when the town is deleted,
     * or {@code false} if it could not be deleted
     */
    @API(status = API.Status.INTERNAL)
    public CompletableFuture<Boolean> delete(TownImpl town) {
        return this.api.db().ctx()
            .deleteFrom(Tables.TOWN)
            .where(Tables.TOWN.ID.eq(town.id()))
            .executeAsync()
            .thenApply(rows -> {
                if (rows > 0) {
                    this.namesToIds.invalidate(town.name());
                    this.cache.synchronous().invalidate(town.id());
                }
                return rows > 0;
            })
            .toCompletableFuture();
    }

    private record TownLoader(BungerTownsImpl api) implements AsyncCacheLoader<Integer, TownImpl> {
        @SuppressWarnings("DataFlowIssue")
        @Override
        public CompletableFuture<TownImpl> asyncLoad(
            Integer id,
            Executor executor
        ) {
            return this.api.db().ctx()
                .select(
                    Tables.TOWN,
                    multiset(
                        DSL.select(Tables.RESIDENT.ID)
                            .from(Tables.RESIDENT)
                            .where(Tables.RESIDENT.TOWN_ID.eq(Tables.TOWN.ID))
                    ))
                .from(Tables.TOWN)
                .where(Tables.TOWN.ID.eq(id))
                .fetchAsync(executor)
                .thenApply(result -> {
                    if (result.isEmpty()) {
                        // No town was found with this ID
                        return null;
                    }
                    final TownRecord record = result.get(0).value1();
                    final List<UUID> residentIds = result.get(0).value2().stream().map(Record1::value1).toList();
                    return new TownImpl(this.api, record, residentIds);
                })
                .toCompletableFuture();
        }

        @Override
        public CompletableFuture<? extends Map<Integer, TownImpl>> asyncLoadAll(
            Set<? extends Integer> keys,
            Executor executor
        ) {
            return this.api.db().ctx()
                .select(
                    Tables.TOWN,
                    multiset(
                        DSL.select(Tables.RESIDENT.ID)
                            .from(Tables.RESIDENT)
                            .where(Tables.RESIDENT.TOWN_ID.eq(Tables.TOWN.ID))
                    )
                )
                .from(Tables.TOWN)
                .where(Tables.TOWN.ID.in(keys))
                .fetchAsync(executor)
                .thenApply(result -> {
                    if (result.size() != keys.size()) {
                        var missing = new HashSet<>(keys);
                        for (var row : result) {
                            missing.remove(row.value1().getId());
                        }
                        this.api.logger().error("Failed to load {} towns with IDs: {}", missing.size(), missing);
                    }

                    final Map<Integer, TownImpl> towns = new HashMap<>();
                    for (var row : result) {
                        final TownRecord town = row.value1();
                        final List<UUID> residentIds = result.get(0).value2().stream().map(Record1::value1).toList();
                        towns.put(town.getId(), new TownImpl(this.api, town, residentIds));
                    }
                    return towns;
                })
                .toCompletableFuture();
        }
    }

    // TODO: consider lowering the expiry time for towns with low resident counts
    private record TownExpiry(BungerTownsImpl api) implements Expiry<Integer, TownImpl> {

        @Override
        public long expireAfterCreate(Integer key, TownImpl value, long currentTime) {
            for (var resident : value.loadedResidents()) {
                if (resident.online) {
                    // Don't expire if any of the town's residents are online
                    return Long.MAX_VALUE;
                }
            }
            // Otherwise, expire after 15 minutes
            return TimeUnit.MINUTES.toNanos(15); // TODO: profile
        }

        @Override
        public long expireAfterUpdate(Integer key, TownImpl value, long currentTime, @NonNegative long currentDuration) {
            for (var resident : value.loadedResidents()) {
                if (resident.online) {
                    // Don't expire if any of the town's residents are online
                    return Long.MAX_VALUE;
                }
            }
            // Otherwise, expire after 15 minutes
            return TimeUnit.MINUTES.toNanos(15); // TODO: profile
        }

        @Override
        public long expireAfterRead(Integer key, TownImpl value, long currentTime, @NonNegative long currentDuration) {
            for (var resident : value.loadedResidents()) {
                if (resident.online) {
                    // Don't expire if any of the town's residents are online
                    return Long.MAX_VALUE;
                }
            }
            // Otherwise, expire after 15 minutes
            return TimeUnit.MINUTES.toNanos(15); // TODO: profile
        }
    }
}
