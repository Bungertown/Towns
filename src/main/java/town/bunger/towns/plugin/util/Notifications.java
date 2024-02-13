package town.bunger.towns.plugin.util;

import net.kyori.adventure.audience.Audience;

import static town.bunger.towns.plugin.util.PermissionAudience.withPermission;

/**
 * {@link Audience}s for notifying players about various events.
 */
public final class Notifications {

    /**
     * An {@link Audience} for notifying players when a town is created.
     */
    public static final PermissionAudience TOWN_CREATED = withPermission("bungertowns.notify.town.created");

    /**
     * An {@link Audience} for notifying players when a town is deleted.
     */
    public static final PermissionAudience TOWN_DELETED = withPermission("bungertowns.notify.town.deleted");

    /**
     * An {@link Audience} for notifying players when a town is opened or closed.
     */
    public static final PermissionAudience TOWN_OPENED = withPermission("bungertowns.notify.town.opened");

    /**
     * An {@link Audience} for notifying players when a town is renamed.
     */
    public static final PermissionAudience TOWN_RENAMED = withPermission("bungertowns.notify.town.renamed");


    private Notifications() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
