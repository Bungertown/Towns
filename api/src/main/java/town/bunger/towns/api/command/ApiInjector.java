package town.bunger.towns.api.command;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.injection.InjectionRequest;
import org.incendo.cloud.injection.InjectionService;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.resident.ResidentManager;
import town.bunger.towns.api.town.TownManager;

public final class ApiInjector<C> implements InjectionService<C> {

    private final BungerTowns api;

    public ApiInjector(BungerTowns api) {
        this.api = api;
    }

    @Override
    public @Nullable Object handle(@NonNull InjectionRequest<C> request) {
        if (request.injectedClass() == BungerTowns.class) {
            return this.api;
        } else if (request.injectedClass() == TownManager.class) {
            return this.api.towns();
        } else if (request.injectedClass() == ResidentManager.class) {
            return this.api.residents();
        } else {
            return null;
        }
    }
}
