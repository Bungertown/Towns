package town.bunger.towns.plugin.command;

import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;
import town.bunger.towns.api.command.ApiInjector;
import town.bunger.towns.api.command.ResidentInjector;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.plugin.command.town.*;

public final class TownCommands {


    public static void register(PaperCommandManager<CommandSender> manager, BungerTownsImpl api) {
        manager.parameterInjectorRegistry()
            .registerInjectionService(new ApiInjector<>(api));
        manager.parameterInjectorRegistry()
            .registerInjector(Logger.class, new LoggerInjector<>(api.logger()));
        manager.parameterInjectorRegistry()
            .registerInjector(Resident.class, new ResidentInjector<>());

        manager.command(new CommandTown<>());
        manager.command(new CommandTownCreate());
        manager.command(new CommandTownInfo<>());
        manager.command(new CommandTownList<>());
        manager.command(new CommandTownOnline());
        manager.command(new CommandTownSetName());
        manager.command(new CommandTownSetSlogan());
    }
}
