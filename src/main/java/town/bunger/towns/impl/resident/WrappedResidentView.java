package town.bunger.towns.impl.resident;

import com.google.gson.JsonObject;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.ResidentView;
import town.bunger.towns.api.town.TownView;
import town.bunger.towns.impl.town.TownImpl;
import town.bunger.towns.impl.town.WrappedTownView;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class WrappedResidentView implements ResidentView {

    private final ResidentImpl resident;

    public WrappedResidentView(ResidentImpl resident) {
        this.resident = resident;
    }

    @Override
    public UUID id() {
        return this.resident.id();
    }

    @Override
    public String name() {
        return this.resident.name();
    }

    @Override
    public Instant created() {
        return this.resident.created();
    }

    @Override
    public @Nullable Instant lastJoined() {
        return this.resident.lastJoined();
    }

    @Override
    public boolean hasTown() {
        return this.resident.hasTown();
    }

    @Override
    public @Nullable TownView town() {
        final TownImpl town = this.resident.town();
        if (town == null) {
            return null;
        }
        return new WrappedTownView(town);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public CompletableFuture<? extends @Nullable TownView> loadTown() {
        return this.resident.loadTown().thenApply(town -> {
            if (town == null) {
                return null;
            }
            return new WrappedTownView(town);
        });
    }

    @Override
    public @Nullable String townName() {
        return this.resident.townName();
    }

    @Override
    public JsonObject metadata() {
        return this.resident.metadata().deepCopy();
    }

    @Override
    public @NotNull Audience audience() {
        return this.resident.audience();
    }
}
