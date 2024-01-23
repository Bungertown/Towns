package town.bunger.towns.plugin.command.town;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import town.bunger.towns.api.command.TownParser;
import town.bunger.towns.api.town.Town;
import town.bunger.towns.plugin.command.TownCommandBean;
import town.bunger.towns.plugin.util.InfoScreen;

import java.util.List;

public final class CommandTownInfo<C extends Audience> extends TownCommandBean<C> {

    @Override
    protected Command.Builder<C> configure(Command.Builder<C> builder) {
        return builder.literal("info", "i")
            .required("town", TownParser.of());
    }

    @Override
    public void execute(@NonNull CommandContext<C> context) {
        final Town town = context.get("town");

        final List<Component> components = InfoScreen.printTown(town);
        for (final Component component : components) {
            context.sender().sendMessage(component);
        }
    }
}
