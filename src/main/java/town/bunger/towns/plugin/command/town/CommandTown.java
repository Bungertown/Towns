package town.bunger.towns.plugin.command.town;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;

import static town.bunger.towns.plugin.i18n.Messages.ERROR_TOWN_MEMBERSHIP_REQUIRED;

public final class CommandTown<C extends CommandSender> extends TownCommandBean<C> {

    @Override
    protected Command.Builder<C> configure(Command.Builder<C> builder) {
        return builder;
    }

    @Override
    public void execute(@NonNull CommandContext<C> context) {
        final Town town = context.inject(Town.class).orElse(null);
        if (town == null) {
            context.sender().sendMessage(ERROR_TOWN_MEMBERSHIP_REQUIRED);
            return;
        }

        final List<Component> components = InfoScreen.printTown(town);
        for (final Component component : components) {
            context.sender().sendMessage(component);
        }
    }
}
