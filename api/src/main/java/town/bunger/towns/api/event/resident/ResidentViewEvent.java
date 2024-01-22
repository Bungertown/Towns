package town.bunger.towns.api.event.resident;

import org.bukkit.event.Event;
import town.bunger.towns.api.resident.ResidentView;

public abstract class ResidentViewEvent extends Event {

    private final ResidentView resident;

    protected ResidentViewEvent(ResidentView resident) {
        this.resident = resident;
    }

    public ResidentView resident() {
        return this.resident;
    }
}
