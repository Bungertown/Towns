package town.bunger.towns.plugin.command;

import cloud.commandframework.CommandComponent;
import cloud.commandframework.arguments.standard.StringParser;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.resident.Resident;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.impl.BungerTownsImpl;
import town.bunger.towns.impl.resident.ResidentImpl;
import town.bunger.towns.plugin.BungerTownsPlugin;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class TownCommands {

    public static void register(PaperCommandManager<CommandSender> manager, BungerTownsPlugin plugin, BungerTownsImpl api) {
        final var base = manager.commandBuilder("town");

        manager.command(base
            .literal("create")
            .argument(
                CommandComponent
                    .ofType(String.class, "name")
                    .parser(StringParser.stringParser())
            )
            .senderType(Player.class)
            .handler(context -> {
                final String name = context.get("name");

                final Resident resident = api.residents().get(context.sender().getUniqueId());
                if (resident == null) {
                    context.sender()
                        .sendMessage(text("Your resident data is not loaded", NamedTextColor.RED));
                    return;
                }

                Town.Builder builder = api.towns().builder()
                    .name(name)
                    .owner(resident);

                Bukkit.getScheduler().runTask(plugin, () -> api.towns().create(builder)
                    .whenComplete((town, ex) -> {
                        if (ex != null) {
                            context.sender()
                                .sendMessage(text("Failed to create town " + name, NamedTextColor.RED));
                            api.logger().error("Failed to create town " + name, ex);
                            return;
                        }

                        context.sender()
                            .sendMessage(text("Created town " + name, NamedTextColor.GREEN));
                    }));
            })
        );
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
                .thenCompose(ResidentImpl::loadTown)
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
