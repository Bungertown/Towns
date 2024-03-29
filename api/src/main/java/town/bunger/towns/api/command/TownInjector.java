package town.bunger.towns.api.command;

import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.injection.ParameterInjector;
import org.incendo.cloud.util.annotation.AnnotationAccessor;
import org.jspecify.annotations.Nullable;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;

/**
 * Injects the {@link Town} of the {@link Resident}.
 *
 * @param <C> Any command sender type
 */
public final class TownInjector<C> implements ParameterInjector<C, Town> {

    @Override
    public @Nullable Town create(CommandContext<C> context, AnnotationAccessor annotationAccessor) {
        final Resident resident = context.inject(Resident.class).orElse(null);
        if (resident == null) {
            return null;
        }
        return resident.town();
    }
}
