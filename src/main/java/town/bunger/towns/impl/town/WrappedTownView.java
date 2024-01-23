package town.bunger.towns.impl.town;

import com.google.gson.JsonObject;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.ResidentView;
import town.bunger.towns.api.town.TownView;
import town.bunger.towns.impl.resident.ResidentImpl;
import town.bunger.towns.impl.resident.WrappedResidentView;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class WrappedTownView implements TownView {

    private final TownImpl town;

    public WrappedTownView(TownImpl town) {
        this.town = town;
    }

    @Override
    public String name() {
        return this.town.name();
    }

    @Override
    public LocalDateTime created() {
        return this.town.created();
    }

    @Override
    public @Nullable ResidentView owner() {
        final ResidentImpl owner = this.town.owner();
        if (owner == null) {
            return null;
        }
        return new WrappedResidentView(owner);
    }

    @Override
    public UUID ownerId() {
        return this.town.ownerId();
    }

    @Override
    public String ownerName() {
        return this.town.ownerName();
    }

    @Override
    public boolean isOpen() {
        return this.town.isOpen();
    }

    @Override
    public boolean isPublic() {
        return this.town.isPublic();
    }

    @Override
    public @Nullable String slogan() {
        return this.town.slogan();
    }

    @Override
    public JsonObject metadata() {
        return this.town.metadata().deepCopy();
    }

    @Override
    public Collection<UUID> residentIds() {
        return this.town.residentIds();
    }

    @Override
    public Collection<String> residentNames() {
        return this.town.residentNames();
    }

    @Override
    public Collection<? extends ResidentView> loadedResidents() {
        return this.town.loadedResidents().stream().map(WrappedResidentView::new).toList();
    }

    @Override
    public CompletableFuture<? extends Collection<? extends ResidentView>> residents() {
        return this.town.residents().thenApply(residents -> residents.stream().map(WrappedResidentView::new).toList());
    }

    @Override
    public boolean hasResident(UUID uuid) {
        return this.town.hasResident(uuid);
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return this.town.audiences();
    }
}
