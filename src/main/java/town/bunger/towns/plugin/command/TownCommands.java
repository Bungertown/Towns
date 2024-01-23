package town.bunger.towns.plugin.command;

import cloud.commandframework.CommandComponent;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import town.bunger.towns.api.command.ApiInjector;
import town.bunger.towns.api.command.ResidentInjector;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.plugin.command.town.CommandTownCreate;
import town.bunger.towns.plugin.command.town.CommandTownList;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class TownCommands {


    public static void register(PaperCommandManager<CommandSender> manager, BungerTownsImpl api) {
        final var base = manager.commandBuilder("town");

        manager.parameterInjectorRegistry()
            .registerInjectionService(new ApiInjector<>(api));
        manager.parameterInjectorRegistry()
            .registerInjector(Logger.class, new LoggerInjector<>(api.logger()));
        manager.parameterInjectorRegistry()
            .registerInjector(Resident.class, new ResidentInjector<>());

        manager.command(new CommandTownList<>());
        manager.command(new CommandTownCreate());
        manager.command(base
            .literal("info")
            .argument(
                CommandComponent.ofType(Town.class, "town")
                    .parser(TownParser.of())
            )
            .handler(context -> {
                final Town town = context.get("town");

                final List<Component> components = InfoScreen.printTown(town);
                for (final Component component : components) {
                    context.sender().sendMessage(component);
                }
            })
        );
        manager.command(base
            .senderType(Player.class)
            .handler(context -> api.residents().load(context.sender().getUniqueId())
                .thenCompose(Resident::loadTown)
                .whenComplete((town, ex) -> {
                    if (ex != null) {
                        context.sender().sendMessage(text("Failed to load your town's data", NamedTextColor.RED));
                        api.logger().error("Failed to load town data for player " + context.sender().getName(), ex);
                        return;
                    }
                    if (town == null) {
                        context.sender().sendMessage(text("You are not in a town.", NamedTextColor.RED));
                        return;
                    }

                    final List<Component> components = InfoScreen.printTown(town);
                    for (final Component component : components) {
                        context.sender().sendMessage(component);
                    }
                })));
    }
}
