package town.bunger.towns.api;

import org.apiguardian.api.API;
import org.jspecify.annotations.Nullable;

public final class BungerTownsProvider {
    private static @Nullable BungerTowns instance = null;

    /**
     * Gets the BungerTowns API instance,
     * throwing {@link IllegalStateException} if it isn't loaded yet.
     *
     * @return The api instance
     * @throws IllegalStateException If the API isn't loaded yet
     */
    public static BungerTowns get() {
        final BungerTowns instance = BungerTownsProvider.instance;
        if (instance == null) {
            throw new IllegalStateException("The BungerTowns API isn't loaded yet!");
        }
        return instance;
    }

    @API(status = API.Status.INTERNAL)
    public static void register(BungerTowns instance) {
        if (BungerTownsProvider.instance != null) {
            throw new IllegalStateException("The BungerTowns API is already loaded!");
        }
        BungerTownsProvider.instance = instance;
    }
}
