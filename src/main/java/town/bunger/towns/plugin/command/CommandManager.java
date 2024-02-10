package town.bunger.towns.plugin.command;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.plugin.BungerTownsPlugin;

public class CommandManager {

    private final PaperCommandManager<CommandSender> manager;

    public CommandManager(BungerTownsPlugin plugin) {
        this.manager = PaperCommandManager.createNative(plugin, ExecutionCoordinator.asyncCoordinator());
        this.manager.registerBrigadier();
        this.manager.registerAsynchronousCompletions();
    }

    public void register(BungerTownsImpl api) {
        ResidentCommands.register(this.manager, api);
        TownCommands.register(this.manager, api);
    }
}
