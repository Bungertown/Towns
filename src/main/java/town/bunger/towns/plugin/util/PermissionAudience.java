package town.bunger.towns.plugin.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link Audience} that includes all online players with a given permission.
 */
public final class PermissionAudience implements ForwardingAudience {

    /**
     * Returns an {@link Audience} that includes all online players with the given permission.
     *
     * @param permission The permission required to be a member of the audience
     * @return The audience
     */
    public static PermissionAudience withPermission(final String permission) {
        return new PermissionAudience(permission);
    }

    private final String permission;

    private PermissionAudience(final String permission) {
        this.permission = permission;
    }

    /**
     * Get the permission required to be a member of this audience.
     *
     * @return The permission
     */
    public String permission() {
        return this.permission;
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return Bukkit.getOnlinePlayers().stream()
            .filter(player -> player.hasPermission(this.permission))
            .toList();
    }
}
