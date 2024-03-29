package town.bunger.towns.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.impl.resident.ResidentImpl;

import java.time.Instant;

public record ResidentListener(BungerTownsImpl api) implements Listener {

    /**
     * {@link EventPriority#LOWEST} so we can pre-fetch the resident data as early as possible.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void prefetchOnLogin(AsyncPlayerPreLoginEvent event) {
        // TODO: some way to bulk pre-fetch residents?
        // Ensure the resident is loaded into the name cache
        this.api.residents().setName(event.getUniqueId(), event.getName());
        // Pre-fetch the resident and resident's town data
        this.api.residents().loadOrCreatePlayer(event.getUniqueId())
            .thenCompose(ResidentImpl::loadTown)
            .whenComplete((resident, ex) -> {
                if (ex != null) {
                    this.api.logger().error("Failed to load resident for player " + event.getName(), ex);
                    return;
                }
                this.api.logger().debug("Loaded resident and town for player {}", event.getName());
            });
    }

    /**
     * {@link EventPriority#MONITOR} so we can update the resident's online status in the cache after other plugins are finished.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void setOnlineOnJoin(PlayerJoinEvent event) {
        final var future = this.api.residents().loadOrCreatePlayer(event.getPlayer().getUniqueId());
        future
            .thenCompose(resident -> resident.setLastJoined(Instant.now()).thenApply($ -> resident))
            .whenComplete((resident, ex) -> {
                if (ex == null) {
                    resident.online = true;
                }
            });
    }

    /**
     * {@link EventPriority#MONITOR} so we can update the resident's online status in the cache after other plugins are finished.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void setOfflineOnLeave(PlayerQuitEvent event) {
        // Online players are guaranteed to be cached
        final var resident = this.api.residents().get(event.getPlayer().getUniqueId());
        if (resident != null) {
            resident.online = false;
            // Need to re-fetch the resident to trigger the cache expiry
            this.api.residents().get(event.getPlayer().getUniqueId());
            // Same for the town to trigger the cache expiry
            final var town = resident.town();
            if (town != null) {
                this.api.towns().get(town.id());
            }
        } else {
            this.api.logger().warn("Player logged off without resident data: {}", event.getPlayer().getName());
        }
    }
}
