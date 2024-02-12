package town.bunger.towns.impl.town;

import com.google.gson.JsonObject;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.ResidentView;
import town.bunger.towns.api.town.TownView;
import town.bunger.towns.impl.resident.WrappedResidentView;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class TownBuilderView implements TownView {

    private final TownImpl.BuilderImpl builder;

    public TownBuilderView(TownImpl.BuilderImpl builder) {
        this.builder = builder;
    }

    @Override
    public String name() {
        return Objects.requireNonNull(this.builder.name, "Town.Builder.name");
    }

    @Override
    public Instant created() {
        return this.builder.created;
    }

    @Override
    public ResidentView owner() {
        return new WrappedResidentView(Objects.requireNonNull(this.builder.owner, "Town.Builder.owner"));
    }

    @Override
    public UUID ownerId() {
        return Objects.requireNonNull(this.builder.owner, "Town.Builder.owner").id();
    }

    @Override
    public String ownerName() {
        return Objects.requireNonNull(this.builder.owner, "Town.Builder.owner").name();
    }

    @Override
    public boolean isOpen() {
        return this.builder.open;
    }

    @Override
    public boolean isPublic() {
        return this.builder.public_;
    }

    @Override
    public @Nullable String slogan() {
        return this.builder.slogan;
    }

    @Override
    public JsonObject metadata() {
        return this.builder.metadata.deepCopy();
    }

    @Override
    public Collection<UUID> residentIds() {
        return List.of(this.owner().id());
    }

    @Override
    public Collection<String> residentNames() {
        return List.of(this.owner().name());
    }

    @Override
    public Collection<? extends ResidentView> loadedResidents() {
        return List.of(this.owner());
    }

    @Override
    public CompletableFuture<? extends Collection<? extends ResidentView>> residents() {
        return CompletableFuture.completedFuture(this.loadedResidents());
    }

    @Override
    public boolean hasResident(UUID uuid) {
        return this.owner().id().equals(uuid);
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return List.of(this.owner());
    }
}
