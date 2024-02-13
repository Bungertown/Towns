package town.bunger.towns.plugin.command;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.slf4j.Logger;
import town.bunger.towns.api.command.ApiInjector;
import town.bunger.towns.api.command.ResidentInjector;
import town.bunger.towns.api.command.TownInjector;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.plugin.BungerTownsPlugin;
import town.bunger.towns.plugin.command.resident.CommandResident;
import town.bunger.towns.plugin.command.town.*;

public class CommandManager {

    private final PaperCommandManager<CommandSender> manager;

    public CommandManager(BungerTownsPlugin plugin) {
        this.manager = PaperCommandManager.createNative(plugin, ExecutionCoordinator.asyncCoordinator());
        this.manager.registerBrigadier();
        this.manager.registerAsynchronousCompletions();
    }

    public void register(BungerTownsImpl api) {
        manager.parameterInjectorRegistry()
            .registerInjectionService(new ApiInjector<>(api));
        manager.parameterInjectorRegistry()
            .registerInjector(Logger.class, new LoggerInjector<>(api.logger()));
        manager.parameterInjectorRegistry()
            .registerInjector(Resident.class, new ResidentInjector<>());
        manager.parameterInjectorRegistry()
            .registerInjector(Town.class, new TownInjector<>());

        registerResidentCommands();
        registerTownCommands();
    }

    public void registerResidentCommands() {
        manager.command(new CommandResident<>());
    }

    public void registerTownCommands() {
        manager.command(new CommandTown<>());
        manager.command(new CommandTownCreate());
        manager.command(new CommandTownDelete());
        manager.command(new CommandTownInfo<>());
        manager.command(new CommandTownJoin());
        manager.command(new CommandTownKick());
        manager.command(new CommandTownLeave());
        manager.command(new CommandTownList<>());
        manager.command(new CommandTownOnline());
        manager.command(new CommandTownResidents());
        manager.command(new CommandTownSetName());
        manager.command(new CommandTownSetOpen());
        manager.command(new CommandTownSetSlogan());
    }
}
