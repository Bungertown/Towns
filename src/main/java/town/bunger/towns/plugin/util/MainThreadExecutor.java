package town.bunger.towns.plugin.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import town.bunger.towns.plugin.BungerTownsPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class MainThreadExecutor implements Executor {

    /**
     * Call an event asynchronously and return a future that completes when the event is called with the cancelled state.
     *
     * <p>If this method is called from the main thread, the event is posted synchronously and returns immediately.
     * If called from a different thread, the event is asynchronously scheduled to be posted and returns when complete.</p>
     *
     * <p>Prefer {@link PluginManager#callEvent(Event)} if always calling from the main thread.</p>
     *
     * @param event The event to call
     * @return A future that completes with the cancelled state of the event
     * @param <T> The type of event
     */
    public static <T extends Event & Cancellable> CompletableFuture<Boolean> callEventAsync(T event) {
        final CompletableFuture<Boolean> cancelledFuture;
        if (Bukkit.isPrimaryThread()) {
            // Optimization: if we're already on the main thread, just call the event and return immediately
            Bukkit.getPluginManager().callEvent(event);
            cancelledFuture = CompletableFuture.completedFuture(event.isCancelled());
        } else {
            // Otherwise, schedule the event to be called asynchronously and return a future
            cancelledFuture = CompletableFuture.supplyAsync(() -> {
                Bukkit.getPluginManager().callEvent(event);
                return event.isCancelled();
            }, INSTANCE);
        }
        return cancelledFuture;
    }

    public static final MainThreadExecutor INSTANCE = new MainThreadExecutor();

    @Override
    public void execute(@NotNull Runnable command) {
        Bukkit.getScheduler().runTask(BungerTownsPlugin.getInstance(), command);
    }

    private MainThreadExecutor() {
    }
}
