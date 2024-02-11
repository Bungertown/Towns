package town.bunger.towns.api.event.town;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import town.bunger.towns.api.town.TownView;

public final class DeleteTownEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final TownView town;
    private boolean cancelled = false;

    public DeleteTownEvent(TownView town) {
        this.town = town;
    }

    public TownView town() {
        return town;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
