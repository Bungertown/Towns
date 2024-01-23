package town.bunger.towns.api.command;

import cloud.commandframework.annotations.AnnotationAccessor;
import cloud.commandframework.annotations.injection.ParameterInjector;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.identity.Identified;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.BungerTowns;
import town.bunger.towns.api.resident.Resident;

import java.util.UUID;

/**
 * Injects the {@link Resident} of the command sender.
 *
 * @param <C> Any command sender type, but requires {@link Identified} to work
 */
public final class ResidentInjector<C> implements ParameterInjector<C, Resident> {

    @Override
    public @Nullable Resident create(
        CommandContext<C> context,
        AnnotationAccessor annotationAccessor
    ) {
        final BungerTowns api = context.inject(BungerTowns.class).orElseThrow();

        final C sender = context.sender();
        if (!(sender instanceof Identified identified)) {
            return null;
        }
        final UUID uuid = identified.identity().uuid();
        return api.residents().get(uuid);
    }
}
