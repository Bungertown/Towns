package town.bunger.towns.plugin.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import town.bunger.towns.plugin.BungerTownsPlugin;

import java.util.concurrent.Executor;

public final class MainThreadExecutor implements Executor {

    public static final MainThreadExecutor INSTANCE = new MainThreadExecutor();

    @Override
    public void execute(@NotNull Runnable command) {
        Bukkit.getScheduler().runTask(BungerTownsPlugin.getInstance(), command);
    }

    private MainThreadExecutor() {
    }
}
