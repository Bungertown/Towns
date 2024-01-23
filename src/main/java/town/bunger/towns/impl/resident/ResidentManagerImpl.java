package town.bunger.towns.impl.resident;

import com.github.benmanes.caffeine.cache.*;
import org.apiguardian.api.API;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jooq.impl.DSL;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.ResidentManager;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.plugin.db.Tables;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public final class ResidentManagerImpl implements ResidentManager {

    private final BungerTownsImpl api;
    private final AsyncLoadingCache<UUID, ResidentImpl> cache;
    private final Cache<UUID, String> uuidsToNames;

    public ResidentManagerImpl(BungerTownsImpl api) {
        this.api = api;

        this.cache = Caffeine.newBuilder()
            .scheduler(Scheduler.systemScheduler())
            .expireAfter(new ResidentExpiry())
            .buildAsync(new ResidentLoader(this.api));
        this.uuidsToNames = Caffeine.newBuilder()
            .<UUID, String>removalListener((key, value, cause) -> {
                if (key != null) {
                    // If the resident is deleted, invalidate the cache
                    this.cache.synchronous().invalidate(key);
                }
            })
            .build();
    }

    /**
     * Loads all resident IDs from the database for performant existence checks and name lookup.
     */
    @API(status = API.Status.INTERNAL)
    public void loadAllIds() {
        this.api.logger().info("Loading all resident IDs...");
        this.api.db().ctx()
            .select(Tables.RESIDENT.ID, Tables.RESIDENT.NAME)
            .from(Tables.RESIDENT)
            .fetchAsync()
            .thenAccept(result -> {
                for (var record : result) {
                    final UUID id = record.value1();
                    final String name = record.value2();
                    this.uuidsToNames.put(id, name);
                }
                this.api.logger().info("Loaded {} resident IDs", result.size());
            });
    }

    /**
     * Sets a resident's name in the cache.
     *
     * @param uuid The resident's UUID
     * @param name The resident's name
     */
    @API(status = API.Status.INTERNAL)
    public void setName(UUID uuid, String name) {
        final String currentName = this.uuidsToNames.getIfPresent(uuid);
        if (currentName != null && currentName.equals(name)) {
            // No change
            return;
        }

        this.uuidsToNames.put(uuid, name);
        if (currentName != null) {
            // If the resident was already registered but with a different name, update the database
            // Residents that are yet to be registered will be inserted when they are first loaded
            this.api.db().ctx()
                .update(Tables.RESIDENT)
                .set(Tables.RESIDENT.NAME, name)
                .where(Tables.RESIDENT.ID.eq(uuid))
                .executeAsync()
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        this.api.logger().error("Failed to update resident name in database", ex);
                    }
                });
        }
    }

    @Override
    public Collection<UUID> all() {
        return List.copyOf(this.uuidsToNames.asMap().keySet());
    }

    @Override
    public Collection<ResidentImpl> loaded() {
        return List.copyOf(this.cache.synchronous().asMap().values());
    }

    @Override
    public @Nullable ResidentImpl get(UUID uuid) {
        return this.cache.synchronous().getIfPresent(uuid);
    }

    @Override
    public @Nullable String getName(UUID uuid) {
        return this.uuidsToNames.getIfPresent(uuid);
    }

    @Override
    public Map<UUID, ResidentImpl> getAll(Collection<UUID> uuids) {
        return this.cache.synchronous().getAll(uuids);
    }

    @Override
    public Map<UUID, String> getAllNames(Collection<UUID> uuids) {
        return this.uuidsToNames.getAllPresent(uuids);
    }

    @Override
    public CompletableFuture<ResidentImpl> load(UUID uuid) {
        if (this.uuidsToNames.getIfPresent(uuid) == null) {
            return CompletableFuture.completedFuture(null);
        }
        return this.cache.get(uuid);
    }

    @Override
    public CompletableFuture<? extends Map<UUID, ResidentImpl>> loadAll(Collection<UUID> uuids) {
        return this.cache.getAll(uuids);
    }

    /**
     * Handles loading a resident from the database when it is not already cached.
     *
     * <p>New residents are automatically created if they do not exist in the database.
     * Existing residents will have their names updated.</p>
     */
    private record ResidentLoader(BungerTownsImpl api) implements AsyncCacheLoader<UUID, ResidentImpl> {
        @SuppressWarnings("DataFlowIssue")
        @Override
        public CompletableFuture<? extends ResidentImpl> asyncLoad(UUID id, Executor executor) {
            final var name = this.api.residents().getName(id);
            // UPSERT the resident to ensure the name is up-to-date
            return this.api.db().ctx()
                .insertInto(Tables.RESIDENT, Tables.RESIDENT.ID, Tables.RESIDENT.NAME)
                .values(id, name)
                .onDuplicateKeyUpdate()
                .set(Tables.RESIDENT.NAME, name)
                .returning()
                .fetchAsync(executor)
                .thenApply(result -> {
                    if (result.isEmpty()) {
                        // No resident was found with this ID
                        return null;
                    }
                    // Update the name cache
                    this.api.residents().uuidsToNames.put(id, name);
                    return new ResidentImpl(this.api, result.get(0));
                }).toCompletableFuture();
        }

        @Override
        public CompletableFuture<? extends Map<? extends UUID, ? extends ResidentImpl>> asyncLoadAll(
            Set<? extends UUID> keys,
            Executor executor
        ) {
            // Mass UPSERT the residents to ensure the names are up-to-date
            final var rows = this.api.residents().uuidsToNames
                .asMap()
                .entrySet()
                .stream()
                .map(entry -> DSL.row(entry.getKey(), entry.getValue()))
                .toList();

            return this.api.db().ctx()
                .insertInto(Tables.RESIDENT, Tables.RESIDENT.ID, Tables.RESIDENT.NAME)
                .valuesOfRows(rows)
                .onDuplicateKeyUpdate()
                .set(Tables.RESIDENT.NAME, DSL.excluded(Tables.RESIDENT.NAME))
                .returning()
                .fetchAsync(executor)
                .thenApply(result -> {
                    if (result.size() != keys.size()) {
                        var missing = new HashSet<>(keys);
                        for (var record : result) {
                            missing.remove(record.getId());
                        }
                        this.api.logger().warn("Failed to load {} residents: {}", missing.size(), missing);
                    }

                    final var map = new HashMap<UUID, ResidentImpl>();
                    for (var record : result) {
                        // Update the name cache
                        this.api.residents().uuidsToNames.put(record.getId(), record.getName());
                        map.put(record.getId(), new ResidentImpl(this.api, record));
                    }
                    return map;
                })
                .toCompletableFuture();
        }
    }

    /**
     * Handles expiring residents from the cache.
     *
     * <p>Residents are expired after 15 minutes of cache inactivity, unless they are online.</p>
     */
    private record ResidentExpiry() implements Expiry<UUID, ResidentImpl> {
        @Override
        public long expireAfterCreate(UUID id, ResidentImpl resident, long currentTime) {
            if (resident.online) {
                // Don't expire if the player is online
                return Long.MAX_VALUE;
            } else {
                // Expire after 15 minutes
                return TimeUnit.MINUTES.toNanos(15); // TODO: profile
            }
        }

        @Override
        public long expireAfterUpdate(UUID id, ResidentImpl resident, long currentTime, @NonNegative long currentDuration) {
            if (resident.online) {
                // Don't expire if the player is online
                return Long.MAX_VALUE;
            } else {
                // Expire after 15 minutes
                return TimeUnit.MINUTES.toNanos(15); // TODO: profile
            }
        }

        @Override
        public long expireAfterRead(UUID id, ResidentImpl resident, long currentTime, @NonNegative long currentDuration) {
            if (resident.online) {
                // Don't expire if the player is online
                return Long.MAX_VALUE;
            } else {
                // Expire after 15 minutes
                return TimeUnit.MINUTES.toNanos(15); // TODO: profile
            }
        }
    }
}
