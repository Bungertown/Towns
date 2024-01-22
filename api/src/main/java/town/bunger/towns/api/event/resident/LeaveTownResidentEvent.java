package town.bunger.towns.api.event.resident;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import town.bunger.towns.api.resident.ResidentView;
import town.bunger.towns.api.town.Town;

public final class LeaveTownResidentEvent extends ResidentViewEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Town town;
    private boolean cancelled = false;

    public LeaveTownResidentEvent(ResidentView resident, Town town) {
        super(resident);
        this.town = town;
    }

    public Town town() {
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
