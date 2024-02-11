package town.bunger.towns.api.event.resident;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import town.bunger.towns.api.resident.ResidentView;
import town.bunger.towns.api.town.TownView;

public final class JoinTownResidentEvent extends ResidentViewEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final TownView town;
    private boolean cancelled = false;

    public JoinTownResidentEvent(ResidentView resident, TownView town) {
        super(resident);
        this.town = town;
    }

    public TownView town() {
        return this.town;
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
