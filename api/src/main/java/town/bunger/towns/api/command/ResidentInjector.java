package town.bunger.towns.api.command;

import cloud.commandframework.annotations.AnnotationAccessor;
import cloud.commandframework.annotations.injection.ParameterInjector;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.identity.Identified;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import town.bunger.towns.api.BungerTownsProvider;
import town.bunger.towns.api.resident.Resident;

import java.util.UUID;

public final class ResidentInjector<C> implements ParameterInjector<C, Resident> {

    @Override
    public @Nullable Resident create(
        @NonNull CommandContext<C> context,
        @NonNull AnnotationAccessor annotationAccessor
    ) {
        final C sender = context.sender();
        if (!(sender instanceof Identified identified)) {
            return null;
        }
        final UUID uuid = identified.identity().uuid();
        return BungerTownsProvider.get().residents().get(uuid);
    }
}
