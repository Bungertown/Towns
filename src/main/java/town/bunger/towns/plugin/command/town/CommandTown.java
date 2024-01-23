package town.bunger.towns.plugin.command.town;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public final class CommandTown<C extends CommandSender> extends TownCommandBean<C> {

    @Override
    protected Command.Builder<C> configure(Command.Builder<C> builder) {
        return builder;
    }

    @Override
    public void execute(@NonNull CommandContext<C> context) {
        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(text("You are not in a town.", NamedTextColor.RED));
            return;
        }

        final List<Component> components = InfoScreen.printTown(town);
        for (final Component component : components) {
            context.sender().sendMessage(component);
        }
    }
}
