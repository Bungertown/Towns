package town.bunger.towns.api.event.town;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.TownView;

public final class CreateTownEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final TownView town;
    private final Resident resident;
    private boolean cancelled = false;

    public CreateTownEvent(TownView town, Resident resident) {
        this.resident = resident;
        this.town = town;
    }

    public TownView town() {
        return this.town;
    }

    public Resident resident() {
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
