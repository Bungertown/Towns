package town.bunger.towns.api.event.town;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import town.bunger.towns.api.resident.ResidentView;
import town.bunger.towns.api.town.TownView;

public final class KickResidentTownEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final TownView town;
    private final ResidentView resident;
    private boolean cancelled = false;

    public KickResidentTownEvent(TownView town, ResidentView resident) {
        this.town = town;
        this.resident = resident;
    }

    public TownView town() {
        return this.town;
    }

    public ResidentView resident() {
        return this.resident;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
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
